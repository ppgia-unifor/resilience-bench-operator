package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.steps.executor.ExecutorStep;
import io.resiliencebench.models.enums.IstioMessages;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.models.service.ResilientService;
import io.resiliencebench.support.Annotations;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Abstract class representing an Istio executor step.
 *
 * @param <TResult> the type of result produced by the executor step
 */
@Service
public abstract class IstioExecutorStep<TResult extends HasMetadata> extends ExecutorStep<TResult> {

  private static final Logger logger = LoggerFactory.getLogger(IstioExecutorStep.class);
  private final IstioClient istioClient;
  private final CustomResourceRepository<ResilientService> serviceRepository;

  /**
   * Constructs a new IstioExecutorStep.
   *
   * @param kubernetesClient the Kubernetes client
   * @param istioClient the Istio client
   * @param serviceRepository the repository for resilient services
   */
  public IstioExecutorStep(
          KubernetesClient kubernetesClient,
          IstioClient istioClient,
          CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient);
    this.istioClient = istioClient;
    this.serviceRepository = serviceRepository;
  }

  /**
   * Gets the Istio client.
   *
   * @return the Istio client
   */
  protected IstioClient getIstioClient() {
    return istioClient;
  }

  /**
   * Finds the VirtualService associated with a given namespace and service name.
   *
   * @param namespace the namespace
   * @param name the name of the service
   * @return the VirtualService, or throws a RuntimeException if not found
   */
  protected VirtualService findVirtualService(String namespace, String name) {
    return serviceRepository.find(namespace, name)
            .map(service -> {
              String virtualServiceName = service.getMetadata().getAnnotations().get(Annotations.VIRTUAL_SERVICE);
              return istioClient.v1beta1().virtualServices()
                      .inNamespace(namespace)
                      .withName(virtualServiceName)
                      .get();
            })
            .orElseThrow(() -> {
              String errorMsg = IstioMessages.SERVICE_NOT_FOUND.format(namespace, name);
              logger.error(errorMsg);
              return new RuntimeException(errorMsg);
            });
  }

  @Override
  public abstract TResult execute(Scenario scenario, ExecutionQueue executionQueue);
}
