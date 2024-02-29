package br.unifor.ppgia.resiliencebench.execution;

import br.unifor.ppgia.resiliencebench.resources.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.resources.queue.Item;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
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

import static br.unifor.ppgia.resiliencebench.support.Annotations.OWNED_BY;
import static java.util.Objects.*;

public class Scheduler implements Watcher<Job> {

  private final static Logger logger = LoggerFactory.getLogger(Scheduler.class);
  private final KubernetesClient kubernetesClient;

  private final CustomResourceRepository<ExecutionQueue> queueCustomResourceRepository;
  private final CustomResourceRepository<Scenario> scenarioRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  public Scheduler(KubernetesClient kubernetesClient, CustomResourceRepository<ExecutionQueue> queueCustomResourceRepository, CustomResourceRepository<Scenario> scenarioRepository, CustomResourceRepository<ExecutionQueue> executionRepository) {
    this.kubernetesClient = kubernetesClient;
    this.queueCustomResourceRepository = queueCustomResourceRepository;
    this.scenarioRepository = scenarioRepository;
    this.executionRepository = executionRepository;
  }

  public Scheduler(KubernetesClient kubernetesClient) {
    this(
            kubernetesClient,
            new CustomResourceRepository<>(kubernetesClient.resources(ExecutionQueue.class)),
            new CustomResourceRepository<>(kubernetesClient.resources(Scenario.class)),
            new CustomResourceRepository<>(kubernetesClient.resources(ExecutionQueue.class)));
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
          runScenario(namespace, nextItem.getScenario());
          updateStatus(nextItem, namespace, "running", executionQueue);
        }
      }
    }
  }

  private boolean existsJobRunning(String namespace) {
    var jobs = kubernetesClient.batch().v1().jobs().inNamespace(namespace).list();
    return jobs.getItems().stream().anyMatch(job ->
            isNull(job.getStatus().getCompletionTime()) && job.getMetadata().getAnnotations().containsKey("resiliencebench.io/scenario"));
  }

  private void runScenario(String namespace, String name) {
    logger.debug("Running scenario: {}", name);
    var runner = new ScenarioExecutor(this.kubernetesClient, new DefaultIstioClient(this.kubernetesClient));
    var job = runner.run(namespace, name);
    var jobsClient = kubernetesClient.batch().v1().jobs();
    job = jobsClient.resource(job).create();
    logger.debug("Job created: {}", job.getMetadata().getName());
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
      if (nonNull(resource.getStatus().getCompletionTime())) {
        logger.debug("Finished job: {}", resource.getMetadata().getName());
        var scenarioName = resource.getMetadata().getAnnotations().get("resiliencebench.io/scenario");
        var scenario = scenarioRepository.get(namespace, scenarioName).get();
        var executionQueue = executionRepository.get(namespace, scenario.getMetadata().getAnnotations().get(OWNED_BY)).get();
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
