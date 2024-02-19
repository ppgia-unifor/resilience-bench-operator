package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.execution.scenario.ScenarioFaultTemplate;
import br.unifor.ppgia.resiliencebench.execution.scenario.ScenarioSpec;
import br.unifor.ppgia.resiliencebench.execution.scenario.ScenarioWorkload;
import br.unifor.ppgia.resiliencebench.external.k6.K6WorkloadAdapter;
import br.unifor.ppgia.resiliencebench.modeling.service.ResilientService;
import br.unifor.ppgia.resiliencebench.modeling.workload.Workload;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjection;
import io.fabric8.istio.api.networking.v1beta1.HTTPRetry;
import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.DefaultIstioClient;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.Map;

import static java.lang.String.format;

public class ScenarioRunner {

  private final KubernetesClient kubernetesClient;
  private final IstioClient istioClient;

  public ScenarioRunner(KubernetesClient kubernetesClient, IstioClient istioClient) {
    this.istioClient = new DefaultIstioClient();
    this.kubernetesClient = kubernetesClient;
  }

  public void run(String namespace, String name) {
    var scenarioRepository = new CustomResourceRepository<>(kubernetesClient, Scenario.class);
    var scenario = scenarioRepository.get(namespace, name);
    if (scenario.isPresent()) {
      internalRun(scenario.get());
    } else {
      throw new RuntimeException(format("Scenario not found: %s.%s", namespace, name));
    }
  }

  private void internalRun(Scenario scenario) {
    var spec = scenario.getSpec();

    // get services
    var targetService =
            findVirtualService(
                    scenario.getMetadata().getNamespace(),
                    spec.getTargetServiceName()
            );

    var sourceService =
            findVirtualService(
                    scenario.getMetadata().getNamespace(),
                    spec.getSourceServiceName()
            );

    // prepare retry pattern
    prepareRetry(spec, sourceService);

    // prepare fault
    prepareFault(spec, targetService);

    // run workload
    var workloadRepository = new CustomResourceRepository<>(kubernetesClient, Workload.class);
    var workload = workloadRepository.get(scenario.getMetadata().getNamespace(), scenario.getSpec().getWorkload().getWorkloadName());
    runWorkload(workload.get(), scenario.getSpec().getWorkload());
  }

  private VirtualService findVirtualService(String namespace, String name) {
    var serviceRepository = new CustomResourceRepository<>(kubernetesClient, ResilientService.class);
    var targetService = serviceRepository.get(namespace, name);

    if (targetService.isPresent()) {
      var virtualServiceName = targetService.get().getMetadata().getAnnotations().get("resiliencebench.io/virtual-service");
      var virtualServiceNamespace = targetService.get().getMetadata().getAnnotations().getOrDefault("resiliencebench.io/virtual-service-ns", "default");
      return istioClient
        .v1beta1()
        .virtualServices()
        .inNamespace(virtualServiceNamespace)
        .withName(virtualServiceName)
        .get();
    } else {
      throw new RuntimeException(format("Service not found: %s.%s", namespace, name));
    }
  }

  private void prepareFault(ScenarioSpec spec, VirtualService targetService) {
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
            .inNamespace(targetService.getMetadata().getNamespace() )
            .resource(newVirtualService)
            .update();
  }

  private void prepareRetry(ScenarioSpec spec, VirtualService targetService) {
    var newVirtualService = targetService
            .edit()
            .editSpec()
            .editFirstHttp()
            .withRetries(configureRetryPattern(spec.getPatternConfig()))
            .endHttp()
            .endSpec()
            .build();

    istioClient
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

  public void runWorkload(Workload workload, ScenarioWorkload scenarioWorkload) {
    var resource = new K6WorkloadAdapter().adapt(workload, scenarioWorkload);
    var created = this.kubernetesClient.resource(resource).inNamespace(workload.getMetadata().getNamespace()).create();
    System.out.println("Created: " + created);
  }
}
