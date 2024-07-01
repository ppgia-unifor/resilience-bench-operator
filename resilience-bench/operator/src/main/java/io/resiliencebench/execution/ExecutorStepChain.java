package io.resiliencebench.execution;

import io.resiliencebench.execution.steps.ExecutorStep;

import java.util.List;

public interface ExecutorStepChain {

  List<ExecutorStep<?>> getPreparationSteps();

  List<ExecutorStep<?>> getPostExecutionSteps();
}
