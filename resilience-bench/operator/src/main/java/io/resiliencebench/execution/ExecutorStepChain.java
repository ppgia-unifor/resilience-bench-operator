package io.resiliencebench.execution;

import java.util.List;

public interface ExecutorStepChain {

  List<ExecutorStep<?>> getPreparationSteps();

  List<ExecutorStep<?>> getPostExecutionSteps();
}
