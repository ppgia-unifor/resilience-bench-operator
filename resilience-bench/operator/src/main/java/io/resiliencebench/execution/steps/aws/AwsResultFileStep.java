package io.resiliencebench.execution.steps.aws;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.steps.ExecutorStep;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;

public class AwsResultFileStep extends ExecutorStep<HasMetadata> {
  public AwsResultFileStep(KubernetesClient kubernetesClient) {
    super(kubernetesClient);
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return false;
  }

  @Override
  protected HasMetadata internalExecute(Scenario scenario, ExecutionQueue queue) {
    return null;
  }
}
