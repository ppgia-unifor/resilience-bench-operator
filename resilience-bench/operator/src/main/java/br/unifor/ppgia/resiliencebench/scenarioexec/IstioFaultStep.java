package br.unifor.ppgia.resiliencebench.scenarioexec;

import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.execution.scenario.ScenarioFaultTemplate;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjection;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class IstioFaultStep extends IstioExecutorStep<VirtualService> {

  public IstioFaultStep(KubernetesClient kubernetesClient, IstioClient istioClient) {
    super(kubernetesClient, istioClient);
  }

  @Override
  public VirtualService execute(Scenario scenario) {
    var targetService =
            findVirtualService(
                    scenario.getMetadata().getNamespace(),
                    scenario.getSpec().getTargetServiceName()
            );

    var fault = configureFault(scenario.getSpec().getFault());

    // TODO Handler error
    // TODO check if the virtual service already has a fault. if yes, update it

    var editedVirtualService = targetService
            .edit()
            .editSpec()
            .editFirstHttp()
            .withFault(fault)
            .endHttp()
            .endSpec()
            .build();

    return istioClient()
            .v1beta1()
            .virtualServices()
            .inNamespace(targetService.getMetadata().getNamespace() )
            .resource(editedVirtualService)
            .update();
  }

  public HTTPFaultInjection configureFault(ScenarioFaultTemplate faultTemplate) {
    var builder = new HTTPFaultInjection().toBuilder();
    if (faultTemplate.getDelay() != null) {
      builder.withNewDelay()
              .withNewPercentage(faultTemplate.getPercentage().doubleValue())
              .withNewHTTPFaultInjectionDelayFixedHttpType(faultTemplate.getDelay().duration() + "ms")
              .endDelay();
    } else {
      builder.withNewAbort()
              .withNewPercentage(faultTemplate.getPercentage().doubleValue())
              .withNewHTTPFaultInjectionAbortHttpStatusErrorType(faultTemplate.getAbort().httpStatus())
              .endAbort();
    }
    return builder.build();
  }
}
