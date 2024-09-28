package io.resiliencebench.execution.steps;

import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.queue.ExecutionQueueItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.resiliencebench.resources.scenario.Scenario;

public abstract class ExecutorStep {

  private final static Logger logger = LoggerFactory.getLogger(ExecutorStep.class);

  private final KubernetesClient kubernetesClient;

  private final Retry retryConfig = RetryRegistry.ofDefaults().retry("executorStep");

  public ExecutorStep(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  protected KubernetesClient kubernetesClient() {
    return kubernetesClient;
  }

  protected abstract boolean isApplicable(Scenario scenario);

  protected abstract void internalExecute(Scenario scenario, ExecutionQueue queue);

  public void execute(Scenario scenario, ExecutionQueue queue) {
    if (isApplicable(scenario)) {
      logger.info("Executing step {}", this.getClass().getSimpleName());
      internalExecute(scenario, queue);
    } else {
      logger.info("Step {} is not applicable for scenario {}", this.getClass().getSimpleName(), scenario.getMetadata().getName());
    }
  }
}
