package io.resiliencebench.execution.steps.k6;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.steps.ExecutorStep;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.scenario.ScenarioWorkload;
import io.resiliencebench.resources.workload.Workload;
import io.resiliencebench.support.CustomResourceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.resiliencebench.support.Annotations.CREATED_BY;

@Service
public class K6LoadGeneratorStep extends ExecutorStep<Job> {

  private final CustomResourceRepository<Workload> workloadRepository;

  public K6LoadGeneratorStep(KubernetesClient kubernetesClient, CustomResourceRepository<Workload> workloadRepository) {
    super(kubernetesClient);
    this.workloadRepository = workloadRepository;
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return true;
  }

  @Override
  protected Job internalExecute(Scenario scenario, ExecutionQueue executionQueue) {
    var workloadName = scenario.getSpec().getWorkload().getWorkloadName();
    var workload = workloadRepository.find(scenario.getMetadata().getNamespace(), workloadName);
    if (workload.isEmpty()) {
      throw new IllegalArgumentException("Workload does not exists: %s".formatted(workloadName));
    }
    return createJob(scenario, workload.get(), scenario.getSpec().getWorkload());
  }

  public ObjectMeta createMeta(Scenario scenario, Workload workload) {
    return new ObjectMetaBuilder()
            .withName(workload.getMetadata().getName() + "-" + scenario.getMetadata().getName())
            .withNamespace(workload.getMetadata().getNamespace())
            .withLabels(Map.of("app", "k6"))
            .addToAnnotations(CREATED_BY, "resiliencebench-operator")
            .addToAnnotations("resiliencebench.io/scenario", scenario.getMetadata().getName())
            .addToAnnotations("resiliencebench.io/workload", workload.getMetadata().getName())
            .build();
  }

  public List<String> createCommand(ScenarioWorkload scenarioWorkload) {
    return Arrays.asList(
            "k6", "run", "/scripts/k6.js", "--vus", String.valueOf(scenarioWorkload.getUsers())
    );
  }

  public Job createJob(Scenario scenario, Workload workload, ScenarioWorkload scenarioWorkload) {
    var meta = createMeta(scenario, workload);
    var job = kubernetesClient().batch().v1().jobs().inNamespace(meta.getNamespace()).withName(meta.getName()).get();
    if (job != null) {
      return job;
    }

    return new JobBuilder()
            .withMetadata(meta)
            .withNewSpec()
            .withNewTemplate()
            .withNewMetadata()
            .addToAnnotations("sidecar.istio.io/inject", "false")
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
  }

  private List<EnvVar> resolveEnvVars(Workload workload, Scenario scenario) {
    List<EnvVar> envs = new ArrayList<>();
    envs.add(new EnvVar("OUTPUT_PATH", String.format("/results/%s", scenario.getMetadata().getName()), null));
    for (var item : workload.getSpec().getOptions()) {
      envs.add(new EnvVar(item.getName(), item.getValue().asText(), null));
    }
    return envs;
  }

  public Container createK6Container(Scenario scenario, ScenarioWorkload scenarioWorkload, Workload workload) {
    var container = new ContainerBuilder()
            .withName("k6")
            .withImage(workload.getSpec().getK6ContainerImage())
            .withCommand("k6", "run", "/scripts/k6.js", "--vus", String.valueOf(scenarioWorkload.getUsers()))
            .withImagePullPolicy("IfNotPresent")
            .withPorts(new ContainerPortBuilder().withContainerPort(6565).build())
            .withVolumeMounts(
                    new VolumeMount("/scripts", "None", "script-volume", false, null, null),
                    new VolumeMount("/results", "HostToContainer", "test-results", false, null, null)
            )
            .withEnv(resolveEnvVars(workload, scenario));

    return container.build();
  }

  public Volume createResultsVolume() {
    return new VolumeBuilder()
            .withName("test-results") // TODO receive it from the workload
            .withNewPersistentVolumeClaim("test-results", false)
            .build();
  }

  public Volume createScriptVolume(Workload workload) {
    return new VolumeBuilder()
            .withName("script-volume") // TODO receive it from the workload
            .withNewConfigMap()
            .withName(workload.getSpec().getScript().getConfigMap().getName())
            .endConfigMap()
            .build();
  }
}
