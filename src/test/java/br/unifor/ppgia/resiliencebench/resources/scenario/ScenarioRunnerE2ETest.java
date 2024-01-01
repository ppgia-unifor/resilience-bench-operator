package br.unifor.ppgia.resiliencebench.resources.scenario;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScenarioRunnerE2ETest {

  @RegisterExtension
  static AbstractOperatorExtension operator = LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(false)
          .oneNamespacePerClass(true)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler())
          .build();

  @Test
  @DisplayName("Should create a scenario")
  public void creationTest() {
    var spec = new ScenarioSpec(
            "target-service-name",
            "source-service-name",
            Map.of("maxAttempts", 10),
            new ScenarioWorkload("workloadName", 100),
            new ScenarioFaultTemplate(25, new DelayFault(100))
    );
    var scenario = new Scenario(spec);
    scenario.setMetadata(new ObjectMetaBuilder().withName("scenario-test").build());
    operator.create(scenario);
    await().atMost(5, MINUTES).untilAsserted(() ->
            assertNotNull(operator.get(Scenario.class, "scenario-test")));
  }
}
