package br.unifor.ppgia.resiliencebench.scenarioexec;

import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.Arrays;

import static java.lang.String.format;

public class ScenarioRunner2 {

  private final KubernetesClient kubernetesClient;
  private final IstioClient istioClient;

  public ScenarioRunner2(KubernetesClient kubernetesClient, IstioClient istioClient) {
    this.istioClient = istioClient;
    this.kubernetesClient = kubernetesClient;
  }

  public Job run(String namespace, String name) {
    var scenarioRepository = new CustomResourceRepository<>(kubernetesClient, Scenario.class);
    var scenario = scenarioRepository.get(namespace, name);
    if (scenario.isPresent()) {
      var preparationSteps = Arrays.asList(
              new IstioRetryStep(kubernetesClient, istioClient),
              // new IstioCircuitBreakerStep(kubernetesClient, istioClient),
              new IstioFaultStep(kubernetesClient, istioClient)
      );

      preparationSteps.forEach(step -> step.execute(scenario.get()));
      var loadGeneratorStep = new K6LoadGeneratorStep(kubernetesClient);
      return loadGeneratorStep.execute(scenario.get());

    } else {
      throw new RuntimeException(format("Scenario not found: %s.%s", namespace, name));
    }
  }
}
