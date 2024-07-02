//package io.resiliencebench.resources.workload;
//
//import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
//import io.fabric8.kubernetes.client.KubernetesClientException;
//import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
//import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
//import io.resiliencebench.controllers.BenchmarkReconciler;
//import io.resiliencebench.controllers.ResilienceServiceReconciler;
//import io.resiliencebench.support.ConfigMapReference;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.RegisterExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//public class WorkloadE2ETest {
//
//  @Autowired
//  private static BenchmarkReconciler benchmarkReconciler;
//
//  @Autowired
//  private static ResilienceServiceReconciler resilienceServiceReconciler;
//
//  @RegisterExtension
//  static AbstractOperatorExtension operator =  LocallyRunOperatorExtension.builder()
//          .waitForNamespaceDeletion(false)
//          .oneNamespacePerClass(true)
//          .withReconciler(resilienceServiceReconciler)
//          .withReconciler(benchmarkReconciler)
//          .build();
//
//  @Test
//  public void creationTest() {
//    final var configMap = new ConfigMapReference("test", "test.js");
//    final var name = "test";
//    final var spec = new WorkloadSpec(List.of(10, 20, 30), 10, "http://local.com", new CloudConfig("token", "projectId"), new ScriptConfig(configMap));
//
//    var workload = new Workload();
//    workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
//    workload.setSpec(spec);
//
//    var workloadClient = operator.resources(Workload.class);
//    workloadClient.resource(workload).create();
//
//    var actualWorkload = workloadClient.withName(name).get();
//    Assertions.assertNotNull(actualWorkload);
//  }
//
//  @Test
//  @DisplayName("Should not create a workload with negative duration")
//  public void testWithNegativeDuration() {
//    final var configMap = new ConfigMapReference("test", "test.js");
//    final var name = "test";
//    final var spec = new WorkloadSpec(List.of(10, 20, 30), -10, "http://local.com", new CloudConfig("token", "projectId"), new ScriptConfig(configMap));
//
//    var workload = new Workload();
//    workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
//    workload.setSpec(spec);
//
//    var workloadClient = operator.resources(Workload.class);
//
//    Assertions.assertThrows(KubernetesClientException.class, () -> workloadClient.resource(workload).create());
//  }
//
//  @Test
//  @DisplayName("Should not create a workload with invalid url")
//  public void testWithInvalidUrl() {
//    final var configMap = new ConfigMapReference("test", "test.js");
//    final var name = "test";
//    final var spec = new WorkloadSpec(List.of(10, 20, 30), 10, "invalid-url", new CloudConfig("token", "projectId"), new ScriptConfig(configMap));
//
//    var workload = new Workload();
//    workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
//    workload.setSpec(spec);
//
//    var workloadClient = operator.resources(Workload.class);
//
//    Assertions.assertThrows(KubernetesClientException.class, () -> workloadClient.resource(workload).create());
//  }
//}
