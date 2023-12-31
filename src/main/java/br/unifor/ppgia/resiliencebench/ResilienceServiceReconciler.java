package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.resilientservice.ResilientService;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.api.networking.v1beta1.VirtualServiceBuilder;
import io.fabric8.istio.client.DefaultIstioClient;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

@ControllerConfiguration
public class ResilienceServiceReconciler implements Reconciler<ResilientService> {

  @Override
  public UpdateControl<ResilientService> reconcile(ResilientService resilientService, Context<ResilientService> context) throws Exception {

    return UpdateControl.noUpdate();
  }

  public VirtualService findVirtualService(IstioClient client, String namespace, String name) {
    return client
            .v1beta1()
            .virtualServices()
//            .withLabelSelector()
            .inNamespace(namespace)
            .withName(name)
            .get();
  }

  public VirtualService create(ResilientService service, String namespace, String name) {
    VirtualService virtualService = new VirtualServiceBuilder()
            .withNewMetadata()
            .withNamespace(namespace)
            .withName(name)
            .endMetadata()
            .withNewSpec()
            .addNewHttp()
            .addNewRoute()
            .withNewDestination()
            .withHost("your-service-name") // Destination service
            .withSubset("v1") // Destination subset
            .endDestination()
            .endRoute()
            .endHttp()
            .endSpec()
            .build();
    return null;
  }
}
