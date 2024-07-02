package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPRetry;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.models.enums.IstioMessages;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.models.scenario.Source;
import io.resiliencebench.models.scenario.Target;
import io.resiliencebench.models.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * Step to configure Istio retry policy for a scenario.
 */
@Service
public class IstioRetryStep extends IstioExecutorStep<Scenario> {

  private static final Logger logger = LoggerFactory.getLogger(IstioRetryStep.class);

  /**
   * Constructs a new IstioRetryStep.
   *
   * @param kubernetesClient the Kubernetes client
   * @param istioClient the Istio client
   * @param serviceRepository the repository for resilient services
   */
  public IstioRetryStep(KubernetesClient kubernetesClient, IstioClient istioClient, CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient, istioClient, serviceRepository);
  }

  /**
   * Executes the Istio retry policy step for the given scenario and execution queue.
   *
   * @param scenario the scenario
   * @param executionQueue the execution queue
   * @return the scenario
   */
  @Override
  public Scenario execute(Scenario scenario, ExecutionQueue executionQueue) {
    scenario.getSpec().getConnectors().forEach(connector ->
            configureRetryOnSource(scenario.getMetadata().getNamespace(), connector.getSource(), connector.getTarget()));
    return scenario;
  }

  /**
   * Configures retry policy on the source service.
   *
   * @param namespace the namespace
   * @param source the source service
   * @param target the target service
   */
  private void configureRetryOnSource(String namespace, Source source, Target target) {
    logger.info(IstioMessages.CONFIGURING_RETRY_ON_SOURCE.format(source.getServiceName(), namespace, target.getServiceName()));

    var sourceVirtualService = findVirtualService(namespace, target.getServiceName());
    var http = sourceVirtualService.getSpec().getHttp().get(0);

    http.setRetries(null);
    createRetryPolicy(source.getPatternConfig()).ifPresent(http::setRetries);

    getIstioClient().v1beta1().virtualServices()
            .inNamespace(sourceVirtualService.getMetadata().getNamespace())
            .resource(sourceVirtualService)
            .update();

    logger.info(IstioMessages.RETRY_CONFIGURED.format(source.getServiceName()));
  }

  /**
   * Creates an HTTPRetry object based on the given pattern configuration.
   *
   * @param patternConfig the pattern configuration
   * @return an Optional containing the HTTPRetry if the configuration is valid, otherwise an empty Optional
   */
  public Optional<HTTPRetry> createRetryPolicy(Map<String, Object> patternConfig) {
    var httpRetry = new HTTPRetry();
    var attempts = (Integer) patternConfig.get("attempts");
    var perTryTimeout = (Integer) patternConfig.get("perTryTimeout");

    if (attempts != null && attempts >= 0) {
      httpRetry.setAttempts(attempts);

      if (perTryTimeout != null && perTryTimeout > 0) {
        httpRetry.setPerTryTimeout(perTryTimeout + "ms");
      } else {
        logger.warn(IstioMessages.RETRY_ATTEMPTS_INVALID.toString());
      }
      return Optional.of(httpRetry);
    } else {
      logger.error(IstioMessages.RETRY_NOT_CONFIGURED.toString());
      return Optional.empty();
    }
  }
}
