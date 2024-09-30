package io.resiliencebench.execution.steps.k6;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.scenario.ScenarioWorkload;
import io.resiliencebench.resources.workload.Workload;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.resiliencebench.support.Annotations.*;

@Service
public class K6JobFactory {

  public K6JobFactory() {
  }

  public Job create(Scenario scenario, Workload workload) {
    var meta = createMeta(scenario, workload);
    return new JobBuilder()
            .withMetadata(meta)
            .withNewSpec()
            .withNewTemplate()
            .withNewMetadata()
            .addToAnnotations("sidecar.istio.io/inject", "false")
            .endMetadata()
            .withNewSpec()
            .withSecurityContext(new PodSecurityContextBuilder().withRunAsUser(1000L).withRunAsGroup(1000L).build())
            .withRestartPolicy("Never")
            .withContainers(createK6Container(scenario, scenario.getSpec().getWorkload(), workload))
            .withVolumes(createResultsVolume(), createScriptVolume(workload))
            .endSpec()
            .endTemplate()
            .withBackoffLimit(4)
            .endSpec()
            .build();
  }

  public ObjectMeta createMeta(Scenario scenario, Workload workload) {
    return new ObjectMetaBuilder()
            .withName(workload.getMetadata().getName() + "-" + scenario.getMetadata().getName())
            .withNamespace(workload.getMetadata().getNamespace())
            .withLabels(Map.of("app", "k6"))
            .addToAnnotations(CREATED_BY, "resiliencebench-operator")
            .addToAnnotations(SCENARIO, scenario.getMetadata().getName())
            .addToAnnotations(WORKLOAD, workload.getMetadata().getName())
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
