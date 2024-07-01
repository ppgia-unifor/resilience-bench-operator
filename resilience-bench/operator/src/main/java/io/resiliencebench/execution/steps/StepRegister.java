package io.resiliencebench.execution.steps;

import io.resiliencebench.execution.steps.istio.IstioCircuitBreakerStep;
import io.resiliencebench.execution.steps.istio.IstioFaultStep;
import io.resiliencebench.execution.steps.istio.IstioRetryStep;
import io.resiliencebench.execution.steps.istio.IstioTimeoutStep;

import java.util.List;

public class StepRegister {

  private final List<ExecutorStep<?>> preparationSteps;
  private final List<ExecutorStep<?>> postExecutionSteps;

  public StepRegister(UpdateStatusQueueStep updateStatusQueueStep,
                      ResultFileStep resultFileStep,
                      IstioCircuitBreakerStep istioCircuitBreakerStep,
                      IstioRetryStep istioRetryStep,
                      IstioTimeoutStep istioTimeoutStep,
                      IstioFaultStep istioFaultStep,
                      EnvironmentStep environmentStep) {

    preparationSteps = List.of(
            updateStatusQueueStep,
            istioRetryStep,
            istioCircuitBreakerStep,
            istioTimeoutStep,
            istioFaultStep,
            environmentStep
    );
    postExecutionSteps = List.of(
            updateStatusQueueStep,
            resultFileStep
    );
  }

  public List<ExecutorStep<?>> getPostExecutionSteps() {
    return postExecutionSteps;
  }

  public List<ExecutorStep<?>> getPreparationSteps() {
    return preparationSteps;
  }
}
