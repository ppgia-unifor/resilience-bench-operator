package br.unifor.ppgia.resiliencebench.istio;

import br.unifor.ppgia.resiliencebench.resources.CustomResourceRepository;
import br.unifor.ppgia.resiliencebench.resources.resilientservice.ResilientService;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioFaultTemplate;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjection;
import io.fabric8.istio.api.networking.v1beta1.HTTPRetry;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.Map;

public class IstioScenarioRunner {

  private final KubernetesClient kubernetesClient;
  private final IstioClient istioClient;

  public IstioScenarioRunner(KubernetesClient kubernetesClient, IstioClient istioClient) {
    this.kubernetesClient = kubernetesClient;
    this.istioClient = istioClient;
  }

  public void runScenario(Scenario scenario) {
//    var spec = scenario.getSpec();
//    var targetServiceMeta = new ObjectMeta();
//    targetServiceMeta.setName(spec.getTargetServiceName());
//    targetServiceMeta.setNamespace(scenario.getMetadata().getNamespace());
//
//    var serviceRepository = new CustomResourceRepository<>(kubernetesClient, ResilientService.class);
//    var targetService = serviceRepository.get(targetServiceMeta);
//
//    var virtualServiceName =
//            targetService.getMetadata().getAnnotations().get("resiliencebench.io/virtual-service");
//
//    var virtualService = istioClient
//            .v1beta1()
//            .virtualServices()
//            .inNamespace(scenario.getMetadata().getNamespace())
//            .withName(virtualServiceName)
//            .get();
//
//    var newVirtualService = virtualService
//            .edit()
//            .editSpec()
//            .editFirstHttp()
//            .withFault(configureFault(spec.getFault()))
//            .withRetries(configureRetryPattern(spec.getPatternConfig()))
//            .endHttp()
//            .endSpec()
//            .build();
//
//    istioClient
//            .v1beta1()
//            .virtualServices()
//            .inNamespace(scenario.getMetadata().getNamespace())
//            .resource(newVirtualService)
//            .update();
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
