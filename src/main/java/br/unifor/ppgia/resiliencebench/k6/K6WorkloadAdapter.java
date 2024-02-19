package br.unifor.ppgia.resiliencebench.k6;

import br.unifor.ppgia.resiliencebench.execution.scenario.ScenarioWorkload;
import br.unifor.ppgia.resiliencebench.modeling.workload.Workload;
import br.unifor.ppgia.resiliencebench.modeling.workload.WorkloadResourceAdapter;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.utils.Serialization;

import java.util.Arrays;
import java.util.Map;

public class K6WorkloadAdapter implements WorkloadResourceAdapter {
  @Override
  public HasMetadata adapt(Workload workload, ScenarioWorkload scenarioWorkload) {
    var args = String.join(" ", Arrays.asList(
            "--vus", String.valueOf(scenarioWorkload.getUsers()),
            "--tag", "workloadName=" + workload.getMetadata().getName(),
            "--duration", workload.getSpec().getDuration() + "s"
    ));

    var spec = Map.of(
            "parallelism", 1,
            "arguments", args,
            "script", Map.of(
                    "config", Map.of(
                            "name", workload.getSpec().getScript().getConfigMap().getName(),
                            "file", workload.getSpec().getScript().getConfigMap().getFile()
                    )
            )
    );

    var meta = new ObjectMetaBuilder()
            .withName(workload.getMetadata().getName())
            .withNamespace(workload.getMetadata().getNamespace())
            .addToAnnotations("resiliencebench.io/created-by", "resiliencebench-operator")
            .addToAnnotations("resiliencebench.io/workload", workload.getMetadata().getName())
            .build();

    return Serialization.unmarshal(Serialization.asJson(Map.of(
            "apiVersion", "k6.io/v1alpha1",
            "kind", "TestRun",
            "metadata", meta,
            "spec", spec
    )), HasMetadata.class);
  }
}
