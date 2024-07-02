package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.models.enums.IstioMessages;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.models.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Step to configure Istio Circuit Breaker for a scenario.
 */
@Service
public class IstioCircuitBreakerStep extends IstioExecutorStep<VirtualService> {

  private static final Logger logger = LoggerFactory.getLogger(IstioCircuitBreakerStep.class);
  private final CustomResourceRepository<ResilientService> serviceRepository;

  /**
   * Constructs a new IstioCircuitBreakerStep.
   *
   * @param kubernetesClient the Kubernetes client
   * @param istioClient the Istio client
   * @param serviceRepository the repository for resilient services
   */
  public IstioCircuitBreakerStep(KubernetesClient kubernetesClient, IstioClient istioClient, CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient, istioClient, serviceRepository);
    this.serviceRepository = serviceRepository;
  }

  /**
   * Executes the Istio Circuit Breaker step for the given scenario and execution queue.
   *
   * @param scenario the scenario
   * @param executionQueue the execution queue
   * @return the configured VirtualService
   */
  @Override
  public VirtualService execute(Scenario scenario, ExecutionQueue executionQueue) {
    validateParameters(scenario, executionQueue);

    ResilientService resilientService = fetchResilientService(scenario);
    if (resilientService == null) {
      logger.error(IstioMessages.RESILIENT_SERVICE_NOT_FOUND.format(scenario.getMetadata().getName()));
      return null;
    }

    VirtualService virtualService = createOrUpdateVirtualService(resilientService);
    logger.info(IstioMessages.CIRCUIT_BREAKER_CONFIGURED.format(resilientService.getMetadata().getName()));
    return virtualService;
  }

  /**
   * Validates the input parameters.
   *
   * @param scenario the scenario
   * @param executionQueue the execution queue
   */
  private void validateParameters(Scenario scenario, ExecutionQueue executionQueue) {
    if (scenario == null || executionQueue == null) {
      throw new IllegalArgumentException(IstioMessages.SCENARIO_AND_EXECUTION_QUEUE_NULL.toString());
    }
  }

  /**
   * Fetches the resilient service associated with the given scenario.
   *
   * @param scenario the scenario
   * @return the resilient service
   */
  private ResilientService fetchResilientService(Scenario scenario) {
    String namespace = scenario.getMetadata().getNamespace();
    String serviceName = scenario.getSpec().getScenario();
    return serviceRepository.find(namespace, serviceName).orElse(null);
  }

  /**
   * Creates or updates the VirtualService for the resilient service.
   *
   * @param resilientService the resilient service
   * @return the configured VirtualService
   */
  private VirtualService createOrUpdateVirtualService(ResilientService resilientService) {
    // Implement the logic to create or update the VirtualService here
    return new VirtualService();
  }
}
