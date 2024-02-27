package br.unifor.ppgia.resiliencebench.scenarioexec;

import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;

public abstract class ExecutorStep<TResult extends HasMetadata> {

  private final KubernetesClient kubernetesClient;

  public ExecutorStep(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  protected KubernetesClient kubernetesClient() {
    return kubernetesClient;
  }

  public abstract TResult execute(Scenario scenario);
}
