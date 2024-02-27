package br.unifor.ppgia.resiliencebench.scenarioexec;

import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;
import io.fabric8.kubernetes.client.KubernetesClient;

public abstract class ScenarioRunnerStep {

  public ScenarioRunnerStep(KubernetesClient kubernetesClient) {
  }

  public abstract void run(Scenario scenario);
}
