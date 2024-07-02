package io.resiliencebench.execution.steps.executor;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;

/**
 * Abstract base class for executing steps in a scenario.
 *
 * @param <TResult> the type of the result produced by this executor step
 */
public abstract class ExecutorStep<TResult extends HasMetadata> {

  private final KubernetesClient kubernetesClient;

  /**
   * Constructs a new ExecutorStep with the given Kubernetes client.
   *
   * @param kubernetesClient the Kubernetes client to use
   */
  public ExecutorStep(final KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  /**
   * Returns the Kubernetes client associated with this executor step.
   *
   * @return the Kubernetes client
   */
  protected KubernetesClient getKubernetesClient() {
    return kubernetesClient;
  }

  /**
   * Executes this step for the given scenario and execution queue.
   *
   * @param scenario the scenario to execute
   * @param queue    the execution queue associated with the scenario
   * @return the result of the execution
   */
  public abstract TResult execute(final Scenario scenario, final ExecutionQueue queue);
}
