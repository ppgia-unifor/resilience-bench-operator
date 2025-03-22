package io.resiliencebench.execution.steps;

import java.util.List;

import org.springframework.stereotype.Component;

import io.resiliencebench.execution.steps.istio.IstioCircuitBreakerStep;
import io.resiliencebench.execution.steps.istio.IstioFaultStep;
import io.resiliencebench.execution.steps.istio.IstioRetryStep;
import io.resiliencebench.execution.steps.istio.IstioTimeoutStep;

import static java.util.List.of;

@Component
public class StepRegistry {

  private final List<ExecutorStep> preparationSteps;
  private final List<ExecutorStep> postExecutionSteps;

  public StepRegistry(UpdateStatusQueueStep updateStatusQueueStep,
                      ResultFileStep resultFileStep,
                      IstioCircuitBreakerStep istioCircuitBreakerStep,
                      IstioRetryStep istioRetryStep,
                      IstioTimeoutStep istioTimeoutStep,
                      IstioFaultStep istioFaultStep,
                      EnvironmentStep environmentStep,
                      EnvironmentPostStep environmentPostStep,
                      ApplicationReadinessStep applicationReadinessStep,
                      ScenarioFaultStep scenarioFaultStep) {

    preparationSteps = of(
            updateStatusQueueStep,
            istioRetryStep,
            istioCircuitBreakerStep,
            istioTimeoutStep,
            istioFaultStep,
            environmentStep,
            applicationReadinessStep,
            scenarioFaultStep);
    postExecutionSteps = of(
            updateStatusQueueStep,
            resultFileStep,
            environmentPostStep);
  }

  public List<ExecutorStep> getPostExecutionSteps() {
    return postExecutionSteps;
  }

  public List<ExecutorStep> getPreparationSteps() {
    return preparationSteps;
  }
}
