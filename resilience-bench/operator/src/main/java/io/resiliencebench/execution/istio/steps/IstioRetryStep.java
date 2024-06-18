package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.*;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.scenario.Source;
import io.resiliencebench.resources.scenario.Target;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class IstioRetryStep extends IstioExecutorStep<Scenario> {

  private final static Logger log = org.slf4j.LoggerFactory.getLogger(IstioRetryStep.class);

  public IstioRetryStep(KubernetesClient kubernetesClient, IstioClient istioClient, CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient, istioClient, serviceRepository);
  }

  @Override
  public Scenario execute(Scenario scenario, ExecutionQueue executionQueue) {
    for (var connector : scenario.getSpec().getConnectors()) {
      var source = connector.getSource();
      configureRetryOnSource(scenario.getMetadata().getNamespace(), source, connector.getTarget());
    }
    return scenario;
  }

  private void configureRetryOnSource(String namespace, Source source, Target target) {
    var sourceVirtualService = findVirtualService(namespace, source.getServiceName());
    var retryPolicy = createRetryPolicy(source.getPatternConfig(), target);
    if (retryPolicy.isPresent()) {
      var destination = new DestinationBuilder().withHost(target.getServiceName()).build();

      var httpRoute = new HTTPRouteBuilder()
              .withRoute(Collections.singletonList(new HTTPRouteDestinationBuilder()
                      .withDestination(destination)
                      .build()))
              .withRetries(retryPolicy.get())
              .build();

      var newVirtualService = sourceVirtualService
              .edit()
              .editSpec()
              .withHttp(httpRoute)
              .endSpec()
              .build();

      istioClient()
              .v1beta1()
              .virtualServices()
              .inNamespace(sourceVirtualService.getMetadata().getNamespace())
              .resource(newVirtualService)
              .update();
    }
  }

  public Optional<HTTPRetry> createRetryPolicy(Map<String, Object> patternConfig, Target target) {
    var httpRetry = new HTTPRetry();
    var attempts = (Integer) patternConfig.get("attempts");
    var perTryTimeout = (Integer) patternConfig.get("perTryTimeout");

    if (attempts != null && attempts >= 0) {
      httpRetry.setAttempts(attempts);

      if (perTryTimeout != null && perTryTimeout > 0) {
        httpRetry.setPerTryTimeout(perTryTimeout + "ms");
      } else {
        log.warn("perTryTimeout must be greater than or equal to 0.");
      }
      return Optional.of(httpRetry);
    } else {
      log.error("Retry not configured. Attempts and perTryTimeout are required for retry pattern configuration.");
      return Optional.empty();
    }
  }
}
