package io.resiliencebench.execution;

import java.util.List;

public interface StepChain {

  List<ExecutorStep<?>> getPreparationSteps();

  List<ExecutorStep<?>> getPostExecutionSteps();
}
