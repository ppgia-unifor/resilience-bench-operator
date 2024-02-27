package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.execution.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.execution.queue.Item;
import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.scenarioexec.ScenarioRunner2;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.fabric8.istio.client.DefaultIstioClient;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import static br.unifor.ppgia.resiliencebench.support.Annotations.OWNED_BY;

public class Scheduler implements Watcher<Job> {

  private final static Logger logger = LoggerFactory.getLogger(Scheduler.class);
  private final KubernetesClient client;

  private final CustomResourceRepository<ExecutionQueue> queueCustomResourceRepository;

  public Scheduler(KubernetesClient client, CustomResourceRepository<ExecutionQueue> queueCustomResourceRepository) {
    this.client = client;
    this.queueCustomResourceRepository = queueCustomResourceRepository;
  }

  public Scheduler(KubernetesClient client) {
    this(client, new CustomResourceRepository<>(client, ExecutionQueue.class));
  }

  private Item getNextItem(ExecutionQueue queue) {
    var items = queue.getSpec().getItems();
    var nextItem = items.stream().filter(item -> !item.isFinished()).findFirst();
    return nextItem.orElse(null);
  }

  public void run(ExecutionQueue queue) {
    var nextItem = getNextItem(queue);
    var namespace = queue.getMetadata().getNamespace();
    if (nextItem != null) {
      var benchmark = queue.getMetadata().getName();
      var executionQueue = queueCustomResourceRepository.get(namespace, benchmark).get(); // TODO tratar erro

      if (nextItem.isPending()) {
        if (!existsJobRunning(namespace)) {
          createJob(namespace, nextItem);
          updateStatus(nextItem, namespace, "running", executionQueue);
        }
      }
    }
  }

  private boolean existsJobRunning(String namespace) {
    var jobs = client.batch().v1().jobs().inNamespace(namespace).list();
    var deve = jobs.getItems().stream().anyMatch(job ->
            Objects.isNull(job.getStatus().getCompletionTime()) && job.getMetadata().getAnnotations().containsKey("scenario"));
    logger.info("Deve processar: {}", deve);
    return deve;
  }

  private void createJob(String namespace, Item item) {
    var runner = new ScenarioRunner2(this.client, new DefaultIstioClient());
    var job = runner.run(namespace, item.getScenario());
    var jobsClient = client.batch().v1().jobs();
    job = jobsClient.resource(job).create();
    jobsClient.resource(job).watch(this);
  }

  private void updateStatus(Item queueItem, String namespace, String status, ExecutionQueue executionQueue) {
    queueItem.setStatus(status);
    queueItem.setStartedAt(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
    executionQueue.getMetadata().setNamespace(namespace); // TODO verificar pq é necessário passar o namespace
    queueCustomResourceRepository.update(executionQueue);
  }

  @Override
  public void eventReceived(Action action, Job resource) { // TODO precisa melhorar esse método. mto emaranhado!
    var namespace = resource.getMetadata().getNamespace();
    if (action.equals(Action.MODIFIED)) {
      if (Objects.nonNull(resource.getStatus().getCompletionTime())) {
        logger.debug("Finished job: {}", resource.getMetadata().getName());
        var scenarioName = resource.getMetadata().getAnnotations().get("resiliencebench.io/scenario");
        var scenario = new CustomResourceRepository<>(client, Scenario.class).get(namespace, scenarioName).get();
        var executionQueue = new CustomResourceRepository<>(client, ExecutionQueue.class).get(namespace, scenario.getMetadata().getAnnotations().get(OWNED_BY)).get();
        var queueItem = executionQueue.getSpec().getItems().stream().filter(item -> item.getScenario().equals(scenarioName)).findFirst().get();
        updateStatus(queueItem, namespace, "finished", executionQueue);
        run(executionQueue);
      }
    }
  }

  @Override
  public void onClose(WatcherException cause) {
    // TODO o que acontece pra cair aqui?
  }
}
