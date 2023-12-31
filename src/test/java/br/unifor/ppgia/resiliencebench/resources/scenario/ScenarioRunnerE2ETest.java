package br.unifor.ppgia.resiliencebench.resources.scenario;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ScenarioRunnerE2ETest {

  final static Logger log = LoggerFactory.getLogger(ScenarioRunnerE2ETest.class);

  final static KubernetesClient client = new DefaultKubernetesClient();

  @RegisterExtension
  AbstractOperatorExtension operator = LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(false)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler())
          .build();

  @Test
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

    var scenarioClient = client.resources(Scenario.class);
    log.info("Creating test Scenario object: {}", scenario);
    scenarioClient.inNamespace(operator.getNamespace()).resource(scenario).create();
  }
}
