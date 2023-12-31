package br.unifor.ppgia.resiliencebench.resources.resilientservice;

import br.unifor.ppgia.resiliencebench.BenchmarkReconciler;
import br.unifor.ppgia.resiliencebench.ResilienceServiceReconciler;
import br.unifor.ppgia.resiliencebench.resources.ConfigMapReference;
import br.unifor.ppgia.resiliencebench.resources.workload.ScriptConfig;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import br.unifor.ppgia.resiliencebench.resources.workload.WorkloadSpec;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.junit.AbstractOperatorExtension;
import io.javaoperatorsdk.operator.junit.LocallyRunOperatorExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Map;

public class ResilientServiceE2ETest {

  final static KubernetesClient client = new DefaultKubernetesClient();

  @RegisterExtension
  AbstractOperatorExtension operator =  LocallyRunOperatorExtension.builder()
          .waitForNamespaceDeletion(false)
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

    var resilientServiceClient = client.resources(ResilientService.class);
    var created = resilientServiceClient.inNamespace(operator.getNamespace()).resource(resilientService).create();
    Assertions.assertNotNull(created);
  }
}
