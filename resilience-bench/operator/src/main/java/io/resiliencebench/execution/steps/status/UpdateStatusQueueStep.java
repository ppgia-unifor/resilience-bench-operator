package io.resiliencebench.execution;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.models.enums.QueueItemStatus;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.queue.QueueItem;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.support.CustomResourceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Service to update the status of the execution queue.
 */
@Service
public class UpdateStatusQueueStep extends ExecutorStep<ExecutionQueue> {

  private final CustomResourceRepository<ExecutionQueue> executionRepository;
  private final String resultFilePathTemplate;

  /**
   * Constructs a new UpdateStatusQueueStep.
   *
   * @param kubernetesClient       the Kubernetes client
   * @param executionRepository    the repository for execution queues
   * @param resultFilePathTemplate the template for the result file path
   */
  public UpdateStatusQueueStep(KubernetesClient kubernetesClient,
          CustomResourceRepository<ExecutionQueue> executionRepository,
          @Value("${resiliencebench.resultFilePathTemplate}") String resultFilePathTemplate) {
    super(kubernetesClient);
    this.executionRepository = executionRepository;
    this.resultFilePathTemplate = resultFilePathTemplate;
  }

  /**
   * Executes the update of the execution queue based on the scenario status.
   *
   * @param scenario        the scenario being executed
   * @param executionQueue  the execution queue to update
   * @return the updated execution queue
   */
  @Override
  public ExecutionQueue execute(Scenario scenario, ExecutionQueue executionQueue) {
    String namespace = scenario.getMetadata().getNamespace();
    QueueItem queueItem = findQueueItem(executionQueue, scenario)
            .orElseThrow(() -> new IllegalStateException("Queue item for scenario " + scenario.getMetadata().getName() + " not found"));

    updateQueueItemStatus(queueItem, scenario);
    executionQueue.getMetadata().setNamespace(namespace); // Ensure namespace is set
    return executionRepository.update(executionQueue);
  }

  /**
   * Finds the queue item associated with the given scenario.
   *
   * @param executionQueue  the execution queue
   * @param scenario        the scenario
   * @return an Optional containing the found QueueItem, or empty if not found
   */
  private Optional<QueueItem> findQueueItem(ExecutionQueue executionQueue, Scenario scenario) {
    return executionQueue.getSpec().getItems().stream()
            .filter(item -> item.getScenario().equals(scenario.getMetadata().getName()))
            .findFirst();
  }

  /**
   * Updates the status of the given queue item based on its current state.
   *
   * @param queueItem the queue item to update
   * @param scenario  the scenario being executed
   */
  private void updateQueueItemStatus(QueueItem queueItem, Scenario scenario) {
    if (queueItem.isRunning()) {
      setQueueItemFinished(queueItem, scenario);
    } else if (queueItem.isPending()) {
      setQueueItemRunning(queueItem);
    }
  }

  /**
   * Sets the queue item's status to "finished" and updates the relevant fields.
   *
   * @param queueItem the queue item to update
   * @param scenario  the scenario being executed
   */
  private void setQueueItemFinished(QueueItem queueItem, Scenario scenario) {
    queueItem.setStatus(QueueItemStatus.FINISHED.getStatus());
    queueItem.setFinishedAt(getCurrentUtcTime());
    queueItem.setResultFile(formatResultFilePath(scenario.getMetadata().getName()));
  }

  /**
   * Sets the queue item's status to "running" and updates the relevant fields.
   *
   * @param queueItem the queue item to update
   */
  private void setQueueItemRunning(QueueItem queueItem) {
    queueItem.setStatus(QueueItemStatus.RUNNING.getStatus());
    queueItem.setStartedAt(getCurrentUtcTime());
  }

  /**
   * Gets the current UTC time as a string.
   *
   * @return the current UTC time
   */
  private String getCurrentUtcTime() {
    return LocalDateTime.now().atZone(ZoneId.of("UTC")).toString();
  }

  /**
   * Formats the result file path using the given scenario name.
   *
   * @param scenarioName the name of the scenario
   * @return the formatted result file path
   */
  private String formatResultFilePath(String scenarioName) {
    return String.format(resultFilePathTemplate, scenarioName);
  }
}