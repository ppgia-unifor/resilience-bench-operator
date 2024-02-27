package br.unifor.ppgia.resiliencebench.resources.service;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;

public class ResilientServiceE2ETest {

  @RegisterExtension
  static AbstractOperatorExtension operator =  LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(false)
          .oneNamespacePerClass(true)
          .withReconciler(new ResilienceServiceReconciler())
          .withReconciler(new BenchmarkReconciler())
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
