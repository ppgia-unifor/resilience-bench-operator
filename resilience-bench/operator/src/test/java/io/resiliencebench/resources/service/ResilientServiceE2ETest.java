package io.resiliencebench.resources.service;

import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import io.resiliencebench.BenchmarkReconciler;
import io.resiliencebench.ResilienceServiceReconciler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

public class ResilientServiceE2ETest {

  private static final KubernetesClient kubernetesClient = new KubernetesClientBuilder().build();

  @RegisterExtension
  static AbstractOperatorExtension operator =  LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(false)
          .oneNamespacePerClass(true)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler(kubernetesClient))
          .build();

  @Test
  public void creationTest() {
    final var name = "test";

    var meta = new ObjectMetaBuilder().withName(name)
            .addToAnnotations("resiliencebench.io/library", "resilience4j")
            .build();
    var selector = new LabelSelector();
    selector.setMatchLabels(Map.of("com.petclinic.service", "api-gateway"));

    var spec = new ResilientServiceSpec();
    spec.setSelector(selector);
    var resilientService = new ResilientService();
    resilientService.setMetadata(meta);
    resilientService.setSpec(spec);

    var resilientServiceClient = operator.resources(ResilientService.class);
    var created = resilientServiceClient.resource(resilientService).create();
    Assertions.assertNotNull(created);
  }
}
