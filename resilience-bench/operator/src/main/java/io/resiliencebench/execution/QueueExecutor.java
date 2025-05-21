package io.resiliencebench.execution;

import io.resiliencebench.resources.queue.ExecutionQueue;

public interface QueueExecutor {
  void execute(ExecutionQueue queue);
}
