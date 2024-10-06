package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.support.CustomResourceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static io.resiliencebench.resources.queue.ExecutionQueueItem.Status.*;
import static java.time.ZoneId.*;

@Service
public class UpdateStatusQueueStep extends ExecutorStep {

  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  public UpdateStatusQueueStep(KubernetesClient kubernetesClient, CustomResourceRepository<ExecutionQueue> executionRepository) {
    super(kubernetesClient);
    this.executionRepository = executionRepository;
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return true;
  }

  @Override
  public void internalExecute(Scenario scenario, ExecutionQueue executionQueue) {
    var namespace = scenario.getMetadata().getNamespace();
    var queueItem = executionQueue.getItem(scenario.getMetadata().getName());
    var now = LocalDateTime.now().atZone(of("UTC")).toString();
    if (queueItem.isRunning()) {
      queueItem.setStatus(FINISHED);
      queueItem.setFinishedAt(now);
    } else if (queueItem.isPending()) {
      queueItem.setStatus(RUNNING);
      queueItem.setStartedAt(now);
    }
    executionQueue.getMetadata().setNamespace(namespace); // TODO verificar pq é necessário passar o namespace
    executionRepository.update(executionQueue);
  }
}
