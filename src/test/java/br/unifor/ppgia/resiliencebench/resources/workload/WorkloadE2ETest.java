package br.unifor.ppgia.resiliencebench.resources.workload;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import br.unifor.ppgia.resiliencebench.resources.ConfigMapReference;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

public class WorkloadE2ETest {

//  @RegisterExtension
//  static AbstractOperatorExtension operator =  LocallyRunOperatorExtension.builder()
//          .waitForNamespaceDeletion(false)
//          .oneNamespacePerClass(true)
//          .withReconciler(new ResilienceServiceReconciler())
//          .withReconciler(new BenchmarkReconciler())
//          .build();
//
//  @Test
//  public void creationTest() {
//    final var configMap = new ConfigMapReference("test", "test.js");
//    final var name = "test";
//    final var spec = new WorkloadSpec(List.of(10, 20, 30), 10, "http://local.com", new ScriptConfig(configMap));
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

//  @Test
//  public void should_not_create_workload_with_negative_duration() {
//    final var configMap = new ConfigMapReference("test", "test.js");
//    final var name = "test";
//    final var spec = new WorkloadSpec(List.of(10, 20, 30), -10, "http://local.com", new ScriptConfig(configMap));
//
//    var workload = new Workload();
//    workload.setMetadata(new ObjectMetaBuilder().withName(name).build());
//    workload.setSpec(spec);
//
//    var workloadClient = client.resources(Workload.class);
//    workloadClient.inNamespace(operator.getNamespace()).resource(workload).create();
//
//    var actualWorkload = workloadClient.inNamespace(operator.getNamespace()).withName(name).get();
//    Assertions.assertNull(actualWorkload);
//  }
}
