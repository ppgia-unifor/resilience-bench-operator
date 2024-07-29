package io.resiliencebench.execution.steps;

import io.resiliencebench.execution.steps.istio.IstioCircuitBreakerStep;
import io.resiliencebench.execution.steps.istio.IstioFaultStep;
import io.resiliencebench.execution.steps.istio.IstioRetryStep;
import io.resiliencebench.execution.steps.istio.IstioTimeoutStep;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepRegister {

  private final List<ExecutorStep<?>> preparationSteps;
  private final List<ExecutorStep<?>> postExecutionSteps;

  public StepRegister(UpdateStatusQueueStep updateStatusQueueStep,
                      ResultLocalFileStep resultLocalFileStep,
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
            resultLocalFileStep
    );
  }

  public List<ExecutorStep<?>> getPostExecutionSteps() {
    return postExecutionSteps;
  }

  public List<ExecutorStep<?>> getPreparationSteps() {
    return preparationSteps;
  }
}
