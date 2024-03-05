package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPRetry;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.scenario.Scenario;

import java.util.Map;

public class IstioRetryStep extends IstioExecutorStep<VirtualService> {

  public IstioRetryStep(KubernetesClient kubernetesClient, IstioClient istioClient) {
    super(kubernetesClient, istioClient);
  }

  @Override
  public VirtualService execute(Scenario scenario) {

    // TODO verify if scenario has a retry pattern configured

    var targetService =
            findVirtualService(
                    scenario.getMetadata().getNamespace(),
                    scenario.getSpec().getTargetServiceName()
            );

    // TODO verify if the virtual service already has a retry. if yes, update it
    var retry = configureRetryPattern(scenario.getSpec().getPatternConfig());

    var newVirtualService = targetService
            .edit()
            .editSpec()
            .editFirstHttp()
            .withRetries(retry)
            .endHttp()
            .endSpec()
            .build();

    return istioClient()
            .v1beta1()
            .virtualServices()
            .inNamespace(targetService.getMetadata().getNamespace())
            .resource(newVirtualService)
            .update();
  }



  public HTTPRetry configureRetryPattern(Map<String, Object> patternConfig) {
    var builder = new HTTPRetry().toBuilder();
    var attempts = (Integer) patternConfig.get("attempts");
    if (attempts != null) {
      builder.withAttempts(attempts);
    } else {
      throw new IllegalArgumentException("attempts is required");
    }
    var perTryTimeout = (Integer) patternConfig.get("perTryTimeout");
    if (perTryTimeout != null) {
      builder.withPerTryTimeout(perTryTimeout + "ms");
    } else {
      throw new IllegalArgumentException("perTryTimeout is required");
    }
    return builder.build();
  }
}
