package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.modeling.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.modeling.service.ResilientService;
import br.unifor.ppgia.resiliencebench.resources.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.Workload;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScenarioRunnerTest {
  @RegisterExtension
  static AbstractOperatorExtension operator = LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(true)
          .oneNamespacePerClass(true)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler())
          .build();

  public static void awaitForCreation(Class<? extends HasMetadata> clazz, String name) {
    await().atMost(5, MINUTES).untilAsserted(() ->
            assertNotNull(operator.resources(clazz).withName(name).get())
    );
  }

  public static <T extends HasMetadata> T create(Class<T> resourceClass, String name) {
    return operator.resources(resourceClass).load(resourceClass.getResourceAsStream("/e2e-scenario-runner/" + name + ".yaml")).create();
  }

  @Test
  public void creationTest() {
    var workload = create(Workload.class, "workload");
    awaitForCreation(Workload.class, workload.getMetadata().getName());

    var service1 = create(ResilientService.class, "productpage-service");
    awaitForCreation(ResilientService.class, service1.getMetadata().getName());

    var service2 = create(ResilientService.class, "ratings-service");
    awaitForCreation(ResilientService.class, service2.getMetadata().getName());

    var benchmark = create(Benchmark.class, "benchmark");
    awaitForCreation(Benchmark.class, benchmark.getMetadata().getName());
    await().atMost(5, MINUTES).untilAsserted(() ->
            assertFalse(operator.resources(Scenario.class).list().getItems().isEmpty()));

    var scenarios = operator.resources(Scenario.class).list().getItems();
    var scenario = scenarios.get(0);

    var runner = new ScenarioRunner(operator.getKubernetesClient(), null);
    runner.run(scenario.getMetadata().getNamespace(), scenario.getMetadata().getName());

//    var resource = operator.resources(Benchmark.class).load(getClass().getResourceAsStream("/benchmark-sample.yaml"));
//    var created = resource.create();
//    assertNotNull(created);
  }
}
