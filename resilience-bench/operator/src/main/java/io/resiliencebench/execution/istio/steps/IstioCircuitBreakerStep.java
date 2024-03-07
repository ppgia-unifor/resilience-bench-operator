package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.VirtualService;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.springframework.stereotype.Service;

@Service
public class IstioCircuitBreakerStep extends IstioExecutorStep<VirtualService> {
  public IstioCircuitBreakerStep(KubernetesClient kubernetesClient, IstioClient istioClient, CustomResourceRepository<ResilientService> serviceRepository) {
    super(kubernetesClient, istioClient, serviceRepository);
  }

  @Override
  public VirtualService execute(Scenario scenario, ExecutionQueue executionQueue) {
    return null;
  }
}
