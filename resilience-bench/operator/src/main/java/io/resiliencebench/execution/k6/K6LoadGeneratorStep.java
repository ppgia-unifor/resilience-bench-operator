package io.resiliencebench.execution.k6;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.steps.executor.ExecutorStep;
import io.resiliencebench.models.enums.K6Messages;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.models.scenario.ScenarioWorkload;
import io.resiliencebench.models.workload.Workload;
import io.resiliencebench.support.Annotations;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Executor step for generating load using K6.
 */
@Service
public class K6LoadGeneratorStep extends ExecutorStep<Job> {

  private static final Logger logger = LoggerFactory.getLogger(K6LoadGeneratorStep.class);

  private final CustomResourceRepository<Workload> workloadRepository;

  @Value("${resiliencebench.resultFilePathTemplate}")
  private String resultFilePathTemplate;

  @Value("${resiliencebench.scriptVolumePath}")
  private String scriptVolumePath;

  @Value("${resiliencebench.resultsVolumePath}")
  private String resultsVolumePath;

  @Value("${resiliencebench.resultsVolumeName}")
  private String resultsVolumeName;

  /**
   * Constructs a new K6LoadGeneratorStep.
   *
   * @param kubernetesClient the Kubernetes client to use
   * @param workloadRepository the repository for workload resources
   */
  public K6LoadGeneratorStep(final KubernetesClient kubernetesClient, final CustomResourceRepository<Workload> workloadRepository) {
    super(kubernetesClient);
    this.workloadRepository = workloadRepository;
  }

  @Override
  public Job execute(final Scenario scenario, final ExecutionQueue executionQueue) {
    final String workloadName = scenario.getSpec().getWorkload().getWorkloadName();
    final Optional<Workload> workloadOpt = workloadRepository.find(scenario.getMetadata().getNamespace(), workloadName);

    if (workloadOpt.isEmpty()) {
      final String errorMessage = K6Messages.WORKLOAD_NOT_FOUND.format(workloadName);
      logger.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }

    return createJob(scenario, workloadOpt.get(), scenario.getSpec().getWorkload());
  }

  /**
   * Creates metadata for a Kubernetes job.
   *
   * @param scenario the scenario associated with the job
   * @param workload the workload associated with the job
   * @return metadata for the job
   */
  public ObjectMeta createMeta(final Scenario scenario, final Workload workload) {
    return new ObjectMetaBuilder()
            .withName(workload.getMetadata().getName() + "-" + scenario.getMetadata().getName())
            .withNamespace(workload.getMetadata().getNamespace())
            .withLabels(Map.of("app", "k6"))
            .addToAnnotations(Annotations.CREATED_BY, "resiliencebench-operator")
            .addToAnnotations(Annotations.SCENARIO, scenario.getMetadata().getName())
            .addToAnnotations(Annotations.WORKLOAD, workload.getMetadata().getName())
            .build();
  }

  /**
   * Creates the command for the K6 container.
   *
   * @param scenarioWorkload the scenario workload
   * @return a list of command strings
   */
  public List<String> createCommand(final ScenarioWorkload scenarioWorkload) {
    return Arrays.asList(
            "k6", "run", "/scripts/k6.js", "--vus", String.valueOf(scenarioWorkload.getUsers())
    );
  }

  /**
   * Creates a Kubernetes job for the scenario and workload.
   *
   * @param scenario the scenario associated with the job
   * @param workload the workload associated with the job
   * @param scenarioWorkload the scenario workload
   * @return the Kubernetes job
   */
  public Job createJob(final Scenario scenario, final Workload workload, final ScenarioWorkload scenarioWorkload) {
    final ObjectMeta meta = createMeta(scenario, workload);
    final Job existingJob = getKubernetesClient().batch().v1().jobs().inNamespace(meta.getNamespace()).withName(meta.getName()).get();

    if (existingJob != null) {
      logger.info(K6Messages.JOB_ALREADY_EXISTS.format(meta.getName()));
      return existingJob;
    }

    Job job = new JobBuilder()
            .withMetadata(meta)
            .withNewSpec()
            .withNewTemplate()
            .withNewMetadata()
            .addToAnnotations(Annotations.ISTIO_SIDECAR_INJECT, "false")
            .endMetadata()
            .withNewSpec()
            .withRestartPolicy("Never")
            .withContainers(createK6Container(scenario, scenarioWorkload, workload))
            .withVolumes(createResultsVolume(), createScriptVolume(workload))
            .endSpec()
            .endTemplate()
            .withBackoffLimit(4)
            .endSpec()
            .build();
    job = getKubernetesClient().batch().v1().jobs().inNamespace(meta.getNamespace()).resource(job).create();
    logger.info(K6Messages.JOB_CREATED.format(job.getMetadata().getName()));
    return job;
  }

  /**
   * Resolves environment variables for the K6 container.
   *
   * @param workload the workload
   * @param scenario the scenario
   * @return a list of environment variables
   */
  private List<EnvVar> resolveEnvVars(final Workload workload, final Scenario scenario) {
    final List<EnvVar> envs = new ArrayList<>();
    envs.add(new EnvVar("OUTPUT_PATH", String.format(resultsVolumePath + "/%s", scenario.getMetadata().getName()), null));

    workload.getSpec().getOptions().forEach(item -> {
      envs.add(new EnvVar(item.getName(), item.getValue().asText(), null));
    });

    return envs;
  }

  /**
   * Creates the K6 container definition.
   *
   * @param scenario the scenario
   * @param scenarioWorkload the scenario workload
   * @param workload the workload
   * @return the container definition
   */
  public Container createK6Container(final Scenario scenario, final ScenarioWorkload scenarioWorkload, final Workload workload) {
    return new ContainerBuilder()
            .withName("k6")
            .withImage(workload.getSpec().getK6ContainerImage())
            .withCommand(createCommand(scenarioWorkload))
            .withImagePullPolicy("IfNotPresent")
            .withPorts(new ContainerPortBuilder().withContainerPort(6565).build())
            .withVolumeMounts(
                    new VolumeMount(scriptVolumePath, null, "script-volume", false, null, null),
                    new VolumeMount(resultsVolumePath, null, resultsVolumeName, false, null, null)
            )
            .withEnv(resolveEnvVars(workload, scenario))
            .build();
  }

  /**
   * Creates the volume for storing test results.
   *
   * @return the volume definition
   */
  public Volume createResultsVolume() {
    return new VolumeBuilder()
            .withName(resultsVolumeName)
            .withNewPersistentVolumeClaim(resultsVolumeName, false)
            .build();
  }

  /**
   * Creates the volume for storing K6 scripts.
   *
   * @param workload the workload associated with the volume
   * @return the volume definition
   */
  public Volume createScriptVolume(final Workload workload) {
    return new VolumeBuilder()
            .withName("script-volume")
            .withNewConfigMap()
            .withName(workload.getSpec().getScript().getConfigMap().getName())
            .endConfigMap()
            .build();
  }
}
