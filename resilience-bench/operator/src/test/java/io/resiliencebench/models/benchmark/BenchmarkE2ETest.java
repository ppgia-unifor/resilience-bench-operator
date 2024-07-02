//package io.resiliencebench.resources.benchmark;
//
//import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
//import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
//import io.resiliencebench.controllers.BenchmarkReconciler;
//import io.resiliencebench.controllers.ResilienceServiceReconciler;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.event.annotation.BeforeTestClass;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//public class BenchmarkE2ETest {
//
//  @BeforeTestClass
//  public static void setUpClass() {
//    System.setProperty("AWS_ACCESS_KEY", "test");
//    System.setProperty("AWS_SECRET_KEY", "test");
//  }
//
//  @Autowired
//  private BenchmarkReconciler benchmarkReconciler;
//
//  @Autowired
//  private ResilienceServiceReconciler resilienceServiceReconciler;
//
//  @BeforeEach
//  public void setUp() {
//    if (operator == null) {
//       operator = LocallyRunOperatorExtension.builder()
//               .waitForNamespaceDeletion(true)
//               .oneNamespacePerClass(true)
//               .withReconciler(resilienceServiceReconciler)
//               .withReconciler(benchmarkReconciler)
//               .build();
//    }
//  }
//
////  @RegisterExtension
//  static AbstractOperatorExtension operator;
//
//  @Test
//  public void creationTest() {
//    var resource = operator.resources(Benchmark.class).load(getClass().getResourceAsStream("/benchmark-sample.yaml"));
//    var created = resource.create();
//    assertNotNull(created);
//  }
//}
