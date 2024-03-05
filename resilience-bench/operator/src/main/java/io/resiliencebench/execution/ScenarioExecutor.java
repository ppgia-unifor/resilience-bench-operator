package io.resiliencebench.execution;

import io.resiliencebench.resources.queue.ExecutionQueue;

public interface ScenarioExecutor {

  void run(ExecutionQueue queue);
}
