package br.unifor.ppgia.resiliencebench.execution.queue;

import br.unifor.ppgia.resiliencebench.modeling.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;

import java.util.List;

public class ExecutionQueueFactory {

  public static ExecutionQueue create(Benchmark benchmark, List<Scenario> scenarios) {
    return new ExecutionQueue();
  }
}
