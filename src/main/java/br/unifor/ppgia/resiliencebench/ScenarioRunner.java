package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.CustomResourceRepository;
import br.unifor.ppgia.resiliencebench.resources.resilientservice.ResilientService;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioFaultTemplate;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioSpec;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjection;
import io.fabric8.istio.api.networking.v1beta1.HTTPRetry;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;

import java.util.Map;

import static java.lang.String.format;

public class ScenarioRunner {

  private final KubernetesClient kubernetesClient;
  private final IstioClient istioClient;

  public ScenarioRunner(KubernetesClient kubernetesClient, IstioClient istioClient) {
    this.kubernetesClient = kubernetesClient;
    this.istioClient = istioClient;
  }

  public void run(String name, String namespace) {
    var scenarioRepository = new CustomResourceRepository<>(kubernetesClient, Scenario.class);
    var scenario = scenarioRepository.get(name, namespace);
    if (scenario.isPresent()) {
      internalRun(scenario.get());
    } else {
      throw new RuntimeException(format("Scenario %s.%s not found", namespace, name));
    }
  }

  private void internalRun(Scenario scenario) {
    var spec = scenario.getSpec();

    // get services
    var targetService =
            findVirtualService(
                    spec.getTargetServiceName(),
                    scenario.getMetadata().getNamespace()
            );

    var sourceService =
            findVirtualService(
                    spec.getSourceServiceName(),
                    scenario.getMetadata().getNamespace()
            );

    // prepare retry pattern
    prepareRetry(scenario.getMetadata().getNamespace(), spec, sourceService);

    // prepare fault
    prepareFault(scenario.getMetadata().getNamespace(), spec, targetService);

    // run workload
    CustomResourceRepository<Workload> workloadRepository = new CustomResourceRepository(kubernetesClient, Workload.class);
    var workload = workloadRepository.get("default", scenario.getSpec().getWorkload().getWorkloadName());
    runWorkload(workload.get());
  }

  private VirtualService findVirtualService(String namespace, String name) {
    var meta = new ObjectMetaBuilder().withName(name).withNamespace(namespace).build();
    var serviceRepository = new CustomResourceRepository<>(kubernetesClient, ResilientService.class);
    var targetService = serviceRepository.get(meta);

    if (targetService.isPresent()) {
      var virtualServiceName = targetService.get().getMetadata().getAnnotations().get("resiliencebench.io/virtual-service");
      return istioClient
        .v1beta1()
        .virtualServices()
        .inNamespace(namespace)
        .withName(virtualServiceName)
        .get();
    } else {
      throw new RuntimeException(format("Service %s.%s not found", namespace, name));
    }
  }

  private void prepareFault(String namespace, ScenarioSpec spec, VirtualService targetService) {
    var newVirtualService = targetService
            .edit()
            .editSpec()
            .editFirstHttp()
            .withFault(configureFault(spec.getFault()))
            .endHttp()
            .endSpec()
            .build();

    istioClient
            .v1beta1()
            .virtualServices()
            .inNamespace(namespace)
            .resource(newVirtualService)
            .update();
  }

  private void prepareRetry(String namespace, ScenarioSpec spec, VirtualService targetService) {
    var newVirtualService = targetService
            .edit()
            .editSpec()
            .editFirstHttp()
            .withRetries(configureRetryPattern(spec.patternConfigInObject()))
            .endHttp()
            .endSpec()
            .build();

    istioClient
            .v1beta1()
            .virtualServices()
            .inNamespace(namespace)
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

  public void runWorkload(Workload workload) {
    var spec = Map.of(
            "parallelism", 1,
            "arguments", "--tag workloadName=" + workload.getMetadata().getName(),
            "script", Map.of(
                    "config", Map.of(
                            "name", workload.getSpec().getScript().getConfigMap().getName(),
                            "file", workload.getSpec().getScript().getConfigMap().getFile()
                    )
            )
    );

    var customResource = Map.of(
            "apiVersion", "k6.io/v1alpha1",
            "kind", "K6",
            "metadata", Map.of("name", workload.getMetadata().getName(), "namespace", workload.getMetadata().getNamespace()),
            "spec", spec
    );

    var resource = Serialization.unmarshal(Serialization.asJson(customResource), HasMetadata.class);
    this.kubernetesClient.resource(resource).inNamespace(workload.getMetadata().getNamespace()).create();
  }
}
