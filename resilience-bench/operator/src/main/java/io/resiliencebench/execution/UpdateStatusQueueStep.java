package io.resiliencebench.execution;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.support.CustomResourceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UpdateStatusQueueStep extends ExecutorStep<ExecutionQueue> {

  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  public UpdateStatusQueueStep(KubernetesClient kubernetesClient, CustomResourceRepository<ExecutionQueue> executionRepository) {
    super(kubernetesClient);
    this.executionRepository = executionRepository;
  }

  @Override
  public ExecutionQueue execute(Scenario scenario, ExecutionQueue executionQueue) {
    var namespace = scenario.getMetadata().getNamespace();
    var queueItem = executionQueue.getSpec().getItems().stream().filter(item -> item.getScenario().equals( scenario.getMetadata().getName())).findFirst().get();

    if (queueItem.isRunning()) {
      queueItem.setStatus("finished");
      queueItem.setFinishedAt(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
      // TODO abstrair construção do path
      queueItem.setResultFile("/results/%s.csv".formatted(scenario.getMetadata().getName()));
    } else if (queueItem.isPending()) {
      queueItem.setStatus("running");
      queueItem.setStartedAt(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    }

    executionQueue.getMetadata().setNamespace(namespace); // TODO verificar pq é necessário passar o namespace
    return executionRepository.update(executionQueue);
  }
}
