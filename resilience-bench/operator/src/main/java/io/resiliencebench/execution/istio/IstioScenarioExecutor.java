package io.resiliencebench.execution.istio;

import static java.util.Objects.isNull;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.steps.executor.ExecutorStep;
import io.resiliencebench.execution.steps.resultFile.ResultFileStep;
import io.resiliencebench.execution.steps.model.ScenarioExecutor;
import io.resiliencebench.execution.steps.status.UpdateStatusQueueStep;
import io.resiliencebench.execution.istio.runner.ScenarioRunner;
import io.resiliencebench.execution.istio.steps.IstioCircuitBreakerStep;
import io.resiliencebench.execution.istio.steps.IstioFaultStep;
import io.resiliencebench.execution.istio.steps.IstioRetryStep;
import io.resiliencebench.execution.k6.K6LoadGeneratorStep;
import io.resiliencebench.models.enums.IstioMessages;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.queue.QueueItem;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.support.Annotations;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for executing Istio scenarios.
 */
@Service
public class IstioScenarioExecutor implements ScenarioExecutor {

  private static final Logger logger = LoggerFactory.getLogger(IstioScenarioExecutor.class);
  private final KubernetesClient kubernetesClient;
  private final CustomResourceRepository<Scenario> scenarioRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;
  private final ScenarioRunner scenarioRunner;

  /**
   * Constructs a new IstioScenarioExecutor.
   *
   * @param kubernetesClient       the Kubernetes client
   * @param scenarioRepository     the repository for scenarios
   * @param executionRepository    the repository for execution queues
   * @param updateStatusQueueStep  the step to update the status of the queue
   * @param istioCircuitBreakerStep the step for Istio circuit breaker
   * @param resultFileStep         the step to handle result files
   * @param istioRetryStep         the step for Istio retries
   * @param istioFaultStep         the step for Istio faults
   * @param k6LoadGeneratorStep    the step to generate load using K6
   */
  public IstioScenarioExecutor(
          KubernetesClient kubernetesClient,
          CustomResourceRepository<Scenario> scenarioRepository,
          CustomResourceRepository<ExecutionQueue> executionRepository,
          UpdateStatusQueueStep updateStatusQueueStep,
          IstioCircuitBreakerStep istioCircuitBreakerStep,
          ResultFileStep resultFileStep,
          IstioRetryStep istioRetryStep,
          IstioFaultStep istioFaultStep,
          K6LoadGeneratorStep k6LoadGeneratorStep) {
    this.kubernetesClient = kubernetesClient;
    this.scenarioRepository = scenarioRepository;
    this.executionRepository = executionRepository;

    List<ExecutorStep<?>> preparationSteps = List.of(updateStatusQueueStep, istioRetryStep, istioCircuitBreakerStep, istioFaultStep);
    List<ExecutorStep<?>> postScenarioExecutionSteps = List.of(updateStatusQueueStep, resultFileStep);

    this.scenarioRunner = new ScenarioRunner(kubernetesClient, scenarioRepository, preparationSteps, postScenarioExecutionSteps, k6LoadGeneratorStep);
  }

  /**
   * Runs the scenarios in the given execution queue.
   *
   * @param queue the execution queue
   */
  @Override
  public void run(ExecutionQueue queue) {
    ExecutionQueue queueToExecute = getExecutionQueue(queue);
    findNextPendingItem(queueToExecute).ifPresentOrElse(
            nextItem -> handlePendingItem(queueToExecute, nextItem),
            () -> handleNoPendingItems(queueToExecute)
    );
  }

  /**
   * Retrieves the next pending item from the execution queue.
   *
   * @param queue the execution queue
   * @return an Optional containing the next pending QueueItem, or empty if none is found
   */
  @Override
  public Optional<QueueItem> findNextPendingItem(ExecutionQueue queue) {
    return ScenarioExecutor.super.findNextPendingItem(queue);
  }

  private ExecutionQueue getExecutionQueue(ExecutionQueue queue) {
    return executionRepository.find(queue.getMetadata())
            .orElseThrow(() -> new RuntimeException(IstioMessages.QUEUE_NOT_FOUND.format(queue.getMetadata().getName())));
  }

  private void handlePendingItem(ExecutionQueue queueToExecute, QueueItem nextItem) {
    String namespace = queueToExecute.getMetadata().getNamespace();
    if (nextItem.isPending() && !isRunning(namespace)) {
      scenarioRunner.runScenario(namespace, nextItem.getScenario(), queueToExecute);
    }
  }

  private void handleNoPendingItems(ExecutionQueue queueToExecute) {
    logger.info(IstioMessages.NO_ITEM_AVAILABLE.format(queueToExecute.getMetadata().getName()));
    if (isAllFinished(queueToExecute)) {
      logger.info(IstioMessages.ALL_ITEMS_FINISHED.format(queueToExecute.getMetadata().getName()));
    }
  }

  private boolean isAllFinished(ExecutionQueue queue) {
    return queue.getSpec().getQueueItems().stream().allMatch(QueueItem::isFinished);
  }

  private boolean isRunning(String namespace) {
    var jobs = kubernetesClient.batch().v1().jobs().inNamespace(namespace).list();
    return jobs.getItems().stream()
            .anyMatch(job -> isNull(job.getStatus().getCompletionTime()) &&
                    job.getMetadata().getAnnotations().containsKey(Annotations.SCENARIO));
  }
}
