package io.resiliencebench.execution;

import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.queue.Item;

import java.util.Optional;

public interface ScenarioExecutor {

  void run(ExecutionQueue queue);

  default Optional<Item> getNextItem(ExecutionQueue queue) {
    var items = queue.getSpec().getItems();
    return items.stream().filter(item -> !item.isFinished()).findFirst();
  }
}
