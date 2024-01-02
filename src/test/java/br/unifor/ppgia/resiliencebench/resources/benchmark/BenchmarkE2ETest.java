package br.unifor.ppgia.resiliencebench.resources.benchmark;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import br.unifor.ppgia.resiliencebench.resources.TestDataGenerator;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

public class BenchmarkE2ETest {

  @RegisterExtension
  static AbstractOperatorExtension operator = LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(true)
          .oneNamespacePerClass(true)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler())
          .build();

  @Test
  public void creationTest() {
    var workload = TestDataGenerator.createWorkload(List.of(10, 20, 30));
    operator.resources(Workload.class).resource(workload).create();
    var benchmark = TestDataGenerator.createBenchmark();
    var newSpec = new BenchmarkSpec(5, workload.getMetadata().getName(), benchmark.getSpec().getConnections());
    benchmark.setSpec(newSpec);
    benchmark.setMetadata(new ObjectMetaBuilder().withName("benchmark-test").build());
    var client = operator.resources(Benchmark.class);
    var created = client.resource(benchmark).create();
    Assertions.assertNotNull(created);
    Assertions.assertEquals(5, created.getSpec().getRounds());
    Assertions.assertEquals("workload", created.getSpec().getWorkload());
    operator.resources(Scenario.class).resources().toList().forEach(System.out::println);
  }
}
