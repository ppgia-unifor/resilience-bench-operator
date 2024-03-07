package io.resiliencebench.execution;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import org.springframework.stereotype.Service;

@Service
public class ResultFileStep extends ExecutorStep<ExecutionQueue> {

  private final FileManager fileManager;

  public ResultFileStep(KubernetesClient kubernetesClient, FileManager fileManager) {
    super(kubernetesClient);
    this.fileManager = fileManager;
  }

  @Override
  public ExecutionQueue execute(Scenario scenario, ExecutionQueue queue) {
    var itemsStream = queue.getSpec().getItems().stream();
    var item = itemsStream.filter(i -> i.getScenario().equals(scenario.getMetadata().getName())).findFirst().orElseThrow(() -> new RuntimeException("Scenario not found in queue"));

    var folder = queue.getMetadata().getCreationTimestamp().replaceAll(":",  "-");

    fileManager.save(item.getResultFile(), folder);

    return queue;
  }
}
