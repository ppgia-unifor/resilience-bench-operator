package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExecutorStep<TResult extends HasMetadata> {

  private final static Logger logger = LoggerFactory.getLogger(ExecutorStep.class);

  private final KubernetesClient kubernetesClient;

  public ExecutorStep(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  protected KubernetesClient kubernetesClient() {
    return kubernetesClient;
  }

  protected abstract boolean isApplicable(Scenario scenario);

  protected abstract TResult internalExecute(Scenario scenario, ExecutionQueue queue);

  public TResult execute(Scenario scenario, ExecutionQueue queue) {
    if (isApplicable(scenario)) {
      logger.info("Executing step {}", this.getClass().getSimpleName());
      return internalExecute(scenario, queue);
    } else {
      logger.info("Step {} is not applicable for scenario {}", this.getClass().getSimpleName(), scenario.getMetadata().getName());
    }
    return null;
  }
}
