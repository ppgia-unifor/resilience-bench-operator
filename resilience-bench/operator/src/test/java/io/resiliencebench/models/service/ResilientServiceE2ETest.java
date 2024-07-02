//package io.resiliencebench.models.service;
//
//import io.fabric8.kubernetes.api.model.LabelSelector;
//import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
//import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
//import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
//import io.resiliencebench.controllers.BenchmarkReconciler;
//import io.resiliencebench.controllers.ResilienceServiceReconciler;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//public class ResilientServiceE2ETest {
//
//  private static final BenchmarkReconciler benchmarkReconciler = new BenchmarkReconciler(null, null, null, null, null);
//  private static final ResilienceServiceReconciler resilienceServiceReconciler = new ResilienceServiceReconciler();
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
//  @DisplayName("Test ResilientService Creation")
//  public void creationTest() {
//    final var name = "test";
//
//    var meta = new ObjectMetaBuilder().withName(name)
//            .addToAnnotations("resiliencebench.io/library", "resilience4j")
//            .build();
//    var selector = new LabelSelector();
//    selector.setMatchLabels(Map.of("com.petclinic.service", "api-gateway"));
//
//    var spec = new ResilientServiceSpec();
//    spec.setSelector(selector);
//    spec.setHost("your-service-name");
//    spec.setSubset("v1");
//
//    var resilientService = new ResilientService();
//    resilientService.setMetadata(meta);
//    resilientService.setSpec(spec);
//
//    var resilientServiceClient = operator.resources(ResilientService.class);
//    var created = resilientServiceClient.resource(resilientService).create();
//
//    assertNotNull(created, "The created ResilientService should not be null");
//    assertNotNull(created.getSpec(), "The created ResilientService's spec should not be null");
//    assertEquals("service-name", created.getSpec().getHost(), "The host should match the specified value");
//    assertEquals("v1", created.getSpec().getSubset(), "The subset should match the specified value");
//  }
//}
