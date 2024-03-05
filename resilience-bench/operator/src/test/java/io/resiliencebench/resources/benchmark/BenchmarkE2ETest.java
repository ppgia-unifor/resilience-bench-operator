package io.resiliencebench.resources.benchmark;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import io.resiliencebench.BenchmarkReconciler;
import io.resiliencebench.ResilienceServiceReconciler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BenchmarkE2ETest {

  private static final KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();

  @RegisterExtension
  static AbstractOperatorExtension operator = LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(true)
          .oneNamespacePerClass(true)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler(kubernetesClient))
          .build();

  @Test
  public void creationTest() {
    var resource = operator.resources(Benchmark.class).load(getClass().getResourceAsStream("/benchmark-sample.yaml"));
    var created = resource.create();
    assertNotNull(created);
  }
}
