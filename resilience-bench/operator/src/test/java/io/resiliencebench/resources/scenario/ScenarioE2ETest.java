//package io.resiliencebench.resources.scenario;
//
//import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
//import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
//import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
//import io.resiliencebench.BenchmarkReconciler;
//import io.resiliencebench.ResilienceServiceReconciler;
//import io.resiliencebench.resources.fault.DelayFault;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Map;
//
//import static java.util.concurrent.TimeUnit.MINUTES;
//import static org.awaitility.Awaitility.await;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class ScenarioE2ETest {
//
//  @Autowired
//  private static BenchmarkReconciler benchmarkReconciler;
//
//  @Autowired
//  private static ResilienceServiceReconciler resilienceServiceReconciler;
//
//  @RegisterExtension
//  static AbstractOperatorExtension operator = LocallyRunOperatorExtension.builder()
//          .waitForNamespaceDeletion(false)
//          .oneNamespacePerClass(true)
//          .withReconciler(resilienceServiceReconciler)
//          .withReconciler(benchmarkReconciler)
//          .build();
//
//  @Test
//  @DisplayName("Should create a scenario")
//  public void creationTest() {
//    var spec = new ScenarioSpec(
//            "target-service-name",
//            "source-service-name",
//            Map.of("maxAttempts", 10),
//            new ScenarioWorkload("workloadName", 100),
//            new ScenarioFaultTemplate(25, new DelayFault(100))
//    );
//    var scenario = new Scenario(spec);
//    scenario.setMetadata(new ObjectMetaBuilder().withName("scenario-test").build());
//    operator.create(scenario);
//    await().atMost(5, MINUTES).untilAsserted(() ->
//            assertNotNull(operator.get(Scenario.class, "scenario-test")));
//  }
//
//  @Test
//  public void testGetPatternConfig() {
//    var resources = operator.resources(Scenario.class);
//    var resource = resources.load(getClass().getResourceAsStream("/scenario-test-get-pattern-config.yaml")).create();
//    var patternConfigExpected = resource.getSpec().getPatternConfig();
//    assertFalse(patternConfigExpected.isEmpty());
//
//    await().atMost(5, MINUTES).untilAsserted(() ->
//            assertNotNull(operator.get(Scenario.class, resource.getMetadata().getName())));
//
//    var createdResource = resources.withName(resource.getMetadata().getName()).get();
//    var patternConfigActual = createdResource.getSpec().getPatternConfig();
//    assertFalse(patternConfigActual.isEmpty());
//    assertEquals(patternConfigExpected, patternConfigActual);
//  }
//
//  @Test
//  public void creationTestFromFile() {
//    var resource = operator.resources(Scenario.class).load(getClass().getResourceAsStream("/scenario-sample.yaml"));
//    var created = resource.create();
//
//    await().atMost(5, MINUTES).untilAsserted(() ->
//            assertNotNull(operator.get(Scenario.class, "example-scenario")));
//  }
//}
