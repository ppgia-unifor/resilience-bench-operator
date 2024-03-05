package io.resiliencebench.execution;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.queue.Item;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.support.CustomResourceRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static io.resiliencebench.support.Annotations.OWNED_BY;

public class UpdateQueueStep extends ExecutorStep<ExecutionQueue> {

  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  public UpdateQueueStep(KubernetesClient kubernetesClient) {
    super(kubernetesClient);
    executionRepository = new CustomResourceRepository<>(kubernetesClient.resources(ExecutionQueue.class));
  }

  @Override
  public ExecutionQueue execute(Scenario scenario) {
    var namespace = scenario.getMetadata().getNamespace();
    var executionQueue = executionRepository.get(namespace, scenario.getMetadata().getAnnotations().get(OWNED_BY)).get();
    var queueItem = executionQueue.getSpec().getItems().stream().filter(item -> item.getScenario().equals( scenario.getMetadata().getName())).findFirst().get();
    var status = resolveNextStatus(queueItem);
    queueItem.setStatus(status);
    queueItem.setStartedAt(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    executionQueue.getMetadata().setNamespace(namespace); // TODO verificar pq é necessário passar o namespace
    executionRepository.update(executionQueue);
    return executionQueue;
  }

  private String resolveNextStatus(Item queueItem) {
    if (queueItem.isPending()) {
      return "running";
    } else if (queueItem.isRunning()) {
      return "finished";
    }
    return "pending";
  }
}
