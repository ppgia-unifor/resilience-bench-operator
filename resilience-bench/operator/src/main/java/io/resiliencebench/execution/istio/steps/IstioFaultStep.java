package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjection;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.scenario.ScenarioFaultTemplate;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IstioFaultStep extends IstioExecutorStep<VirtualService> {

  private final static Logger logger = LoggerFactory.getLogger(IstioFaultStep.class);

  public IstioFaultStep(KubernetesClient kubernetesClient, IstioClient istioClient, CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient, istioClient, serviceRepository);
  }

  @Override
  public VirtualService execute(Scenario scenario, ExecutionQueue executionQueue) {
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
    if (faultTemplate == null || (faultTemplate.getAbort() == null && faultTemplate.getDelay() == null)) {
      logger.error("Fault template is null. No fault was configured.");
      return null;
    }

    var builder = new HTTPFaultInjection().toBuilder();
    if (faultTemplate.getDelay() != null) {
      builder.withNewDelay()
              .withNewPercentage(faultTemplate.getPercentage().doubleValue())
              .withNewHTTPFaultInjectionDelayFixedHttpType(faultTemplate.getDelay().duration() + "ms")
              .endDelay();
    } else if (faultTemplate.getAbort() != null) {
      builder.withNewAbort()
              .withNewPercentage(faultTemplate.getPercentage().doubleValue())
              .withNewHTTPFaultInjectionAbortHttpStatusErrorType(faultTemplate.getAbort().httpStatus())
              .endAbort();
    }
    return builder.build();
  }
}