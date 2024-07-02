package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjection;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.models.enums.IstioMessages;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.models.scenario.ScenarioFaultTemplate;
import io.resiliencebench.models.scenario.Target;
import io.resiliencebench.models.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Step to configure Istio fault injection for a scenario.
 */
@Service
public class IstioFaultStep extends IstioExecutorStep<Scenario> {

  private static final Logger logger = LoggerFactory.getLogger(IstioFaultStep.class);

  /**
   * Constructs a new IstioFaultStep.
   *
   * @param kubernetesClient the Kubernetes client
   * @param istioClient the Istio client
   * @param serviceRepository the repository for resilient services
   */
  public IstioFaultStep(KubernetesClient kubernetesClient, IstioClient istioClient, CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient, istioClient, serviceRepository);
  }

  /**
   * Executes the Istio fault injection step for the given scenario and execution queue.
   *
   * @param scenario the scenario
   * @param executionQueue the execution queue
   * @return the scenario
   */
  @Override
  public Scenario execute(Scenario scenario, ExecutionQueue executionQueue) {
    scenario.getSpec().getConnectors().forEach(connector -> configureFaultOnTarget(scenario.getMetadata().getNamespace(), connector.getTarget()));
    return scenario;
  }

  /**
   * Configures fault injection on the target service.
   *
   * @param namespace the namespace
   * @param target the target service
   */
  private void configureFaultOnTarget(String namespace, Target target) {
    logger.info(IstioMessages.CONFIGURING_FAULT_ON_TARGET.format(target.getServiceName(), namespace));
    var targetService = findVirtualService(namespace, target.getServiceName());
    var virtualService = targetService.edit().editSpec().editFirstHttp();

    createFault(target.getFault()).ifPresent(virtualService::withFault);

    var editedVirtualService = virtualService.endHttp().endSpec().build();
    getIstioClient().v1beta1().virtualServices().inNamespace(namespace).resource(editedVirtualService).update();

    logger.info(IstioMessages.FAULT_CONFIGURED.format(target.getServiceName()));
  }

  /**
   * Creates an HTTPFaultInjection object based on the given fault template.
   *
   * @param faultTemplate the fault template
   * @return an Optional containing the HTTPFaultInjection if the template is valid, otherwise an empty Optional
   */
  public Optional<HTTPFaultInjection> createFault(ScenarioFaultTemplate faultTemplate) {
    if (faultTemplate == null || (faultTemplate.getAbort() == null && faultTemplate.getDelay() == null)) {
      logger.error(IstioMessages.FAULT_TEMPLATE_NULL.toString());
      return Optional.empty();
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
    return Optional.of(builder.build());
  }
}
