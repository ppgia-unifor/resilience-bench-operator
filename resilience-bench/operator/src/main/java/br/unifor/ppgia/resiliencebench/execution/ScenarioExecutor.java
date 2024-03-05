package br.unifor.ppgia.resiliencebench.execution;

import br.unifor.ppgia.resiliencebench.resources.queue.ExecutionQueue;

public interface ScenarioExecutor {

  void run(ExecutionQueue queue);
}
