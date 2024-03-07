package io.resiliencebench.execution.k6;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.ExecutorStep;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.scenario.ScenarioWorkload;
import io.resiliencebench.resources.workload.Workload;
import io.resiliencebench.support.CustomResourceRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.resiliencebench.support.Annotations.CREATED_BY;

@Service
public class K6LoadGeneratorStep extends ExecutorStep<Job> {

  private final CustomResourceRepository<Workload> workloadRepository;

  public K6LoadGeneratorStep(KubernetesClient kubernetesClient, CustomResourceRepository<Workload> workloadRepository) {
    super(kubernetesClient);
    this.workloadRepository = workloadRepository;
  }

  @Override
  public Job execute(Scenario scenario, ExecutionQueue executionQueue) {
    var workload = workloadRepository.get(scenario.getMetadata().getNamespace(), scenario.getSpec().getWorkload().getWorkloadName());
    return createJob(scenario, workload.get(), scenario.getSpec().getWorkload()); // TODO verify if workload exists
  }

  public ObjectMeta createMeta(Scenario scenario, Workload workload) {
    return new ObjectMetaBuilder()
            .withName(workload.getMetadata().getName() + "-" + UUID.fromString(scenario.getMetadata().getUid()))
            .withNamespace(workload.getMetadata().getNamespace())
            .addToAnnotations(CREATED_BY, "resiliencebench-operator")
            .addToAnnotations("resiliencebench.io/scenario", scenario.getMetadata().getName())
            .addToAnnotations("resiliencebench.io/workload", workload.getMetadata().getName())
            .build();
  }

  public List<String> createCommand(Scenario scenario, ScenarioWorkload scenarioWorkload, Workload workload) {
    return Arrays.asList(
            "k6", "run", "/scripts/k6.js",
            "--out", String.format("json=/results/%s.json", scenario.getMetadata().getName()),
            "--vus", String.valueOf(scenarioWorkload.getUsers()),
            "--tag", "workloadName=" + workload.getMetadata().getName(),
            "--duration", workload.getSpec().getDuration() + "s"
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

  public Container createK6Container(Scenario scenario, ScenarioWorkload scenarioWorkload, Workload workload) {
    return new ContainerBuilder()
            .withName("k6")
            .withImage("grafana/k6")
            .withCommand(createCommand(scenario, scenarioWorkload, workload))
            .withVolumeMounts(
                    new VolumeMount("/scripts", "None", "script-volume", false, null, null),
                    new VolumeMount("/results", "HostToContainer", "test-results", false, null, null)
            )
            .build();
  }

  public Volume createResultsVolume() {
    return new VolumeBuilder()
            .withName("test-results")
            .withNewPersistentVolumeClaim("test-results", false)
            .build();
  }

  public Volume createScriptVolume(Workload workload) {
    return new VolumeBuilder()
            .withName("script-volume")
            .withNewConfigMap()
            .withName(workload.getSpec().getScript().getConfigMap().getName())
            .endConfigMap()
            .build();
  }
}
