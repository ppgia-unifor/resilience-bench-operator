package br.unifor.ppgia.resiliencebench.resources.execution.queue;

import br.unifor.ppgia.resiliencebench.resources.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.modeling.benchmark.Benchmark;

import java.util.List;

public class ExecutionQueueFactory {

  public static ExecutionQueue create(Benchmark benchmark, List<Scenario> scenarios) {
    return new ExecutionQueue();
  }
}
