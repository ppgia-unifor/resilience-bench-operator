package br.unifor.ppgia.resiliencebench.resources.workload;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import br.unifor.ppgia.resiliencebench.resources.ConfigMapReference;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.ScriptConfig;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.Workload;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.WorkloadSpec;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

public class WorkloadE2ETest {

  @RegisterExtension
  static AbstractOperatorExtension operator =  LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(false)
          .oneNamespacePerClass(true)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler())
          .build();

  @Test
  public void creationTest() {
    final var configMap = new ConfigMapReference("test", "test.js");
    final var name = "test";
    final var spec = new WorkloadSpec(List.of(10, 20, 30), 10, "http://local.com", new ScriptConfig(configMap));

    var workload = new Workload();
    workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
    workload.setSpec(spec);

    var workloadClient = operator.resources(Workload.class);
    workloadClient.resource(workload).create();

    var actualWorkload = workloadClient.withName(name).get();
    Assertions.assertNotNull(actualWorkload);
  }

  @Test
  @DisplayName("Should not create a workload with negative duration")
  public void testWithNegativeDuration() {
    final var configMap = new ConfigMapReference("test", "test.js");
    final var name = "test";
    final var spec = new WorkloadSpec(List.of(10, 20, 30), -10, "http://local.com", new ScriptConfig(configMap));

    var workload = new Workload();
    workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
    workload.setSpec(spec);

    var workloadClient = operator.resources(Workload.class);

    Assertions.assertThrows(KubernetesClientException.class, () -> workloadClient.resource(workload).create());
  }

  @Test
  @DisplayName("Should not create a workload with invalid url")
  public void testWithInvalidUrl() {
    final var configMap = new ConfigMapReference("test", "test.js");
    final var name = "test";
    final var spec = new WorkloadSpec(List.of(10, 20, 30), 10, "invalid-url", new ScriptConfig(configMap));

    var workload = new Workload();
    workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
    workload.setSpec(spec);

    var workloadClient = operator.resources(Workload.class);

    Assertions.assertThrows(KubernetesClientException.class, () -> workloadClient.resource(workload).create());
  }
}
