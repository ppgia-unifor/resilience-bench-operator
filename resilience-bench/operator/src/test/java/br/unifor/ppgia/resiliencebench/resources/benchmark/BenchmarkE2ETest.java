package br.unifor.ppgia.resiliencebench.resources.benchmark;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
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