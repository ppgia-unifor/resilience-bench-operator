package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPRetry;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class IstioRetryStep extends IstioExecutorStep<VirtualService> {

  private final static Logger log = org.slf4j.LoggerFactory.getLogger(IstioRetryStep.class);

  public IstioRetryStep(KubernetesClient kubernetesClient, IstioClient istioClient, CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient, istioClient, serviceRepository);
  }

  @Override
  public VirtualService execute(Scenario scenario, ExecutionQueue executionQueue) {

    // TODO verify if scenario has a retry pattern configured

    var targetService =
            findVirtualService(
                    scenario.getMetadata().getNamespace(),
                    scenario.getSpec().getSourceServiceName()
            );

    var retry = configureRetryPattern(scenario.getSpec().getPatternConfig());

    if (retry.isPresent()) {
      var newVirtualService = targetService
              .edit()
              .editSpec()
              .editFirstHttp()
              .withRetries(retry.get())
              .endHttp()
              .endSpec()
              .build();

      istioClient()
              .v1beta1()
              .virtualServices()
              .inNamespace(targetService.getMetadata().getNamespace())
              .resource(newVirtualService)
              .update();
      return newVirtualService;
    } else {
      return targetService;
    }
  }



  public Optional<HTTPRetry> configureRetryPattern(Map<String, Object> patternConfig) {
    var builder = new HTTPRetry().toBuilder();
    var attempts = (Integer) patternConfig.get("attempts");
    var perTryTimeout = (Integer) patternConfig.get("perTryTimeout");

    if (attempts != null && perTryTimeout != null) {
      builder.withAttempts(attempts);

      if (perTryTimeout >= 0) {
        builder.withPerTryTimeout(perTryTimeout + "ms");
      } else {
        log.warn("perTryTimeout must be greater than or equal to 0.");
      }
      return Optional.of(builder.build());
    } else {
      log.error("Retry not configured. Attempts and perTryTimeout are required for retry pattern configuration.");
      return Optional.empty();
    }
  }
}
