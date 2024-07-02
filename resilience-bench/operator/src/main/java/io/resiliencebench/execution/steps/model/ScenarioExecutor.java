package io.resiliencebench.execution;

import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.queue.QueueItem;

import java.util.Optional;

/**
 * Interface for executing scenarios from an execution queue.
 */
public interface ScenarioExecutor {

  /**
   * Executes the scenarios in the provided execution queue.
   *
   * @param queue the execution queue containing scenarios to execute
   */
  void run(ExecutionQueue queue);

  /**
   * Retrieves the next pending item from the execution queue.
   *
   * @param queue the execution queue
   * @return an Optional containing the next pending QueueItem, or empty if none is found
   */
  default Optional<QueueItem> findNextPendingItem(ExecutionQueue queue) {
    return queue.getSpec().getItems().stream()
            .filter(QueueItem::isPending)
            .findFirst();
  }
}
