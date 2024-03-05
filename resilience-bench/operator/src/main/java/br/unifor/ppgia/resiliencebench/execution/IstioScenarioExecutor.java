package br.unifor.ppgia.resiliencebench.execution;

import br.unifor.ppgia.resiliencebench.execution.steps.*;
import br.unifor.ppgia.resiliencebench.resources.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.resources.queue.Item;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.fabric8.istio.client.DefaultIstioClient;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static br.unifor.ppgia.resiliencebench.support.Annotations.OWNED_BY;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class IstioScenarioExecutor implements Watcher<Job>, ScenarioExecutor {

  private final static Logger logger = LoggerFactory.getLogger(IstioScenarioExecutor.class);
  private final KubernetesClient kubernetesClient;

  private final IstioClient istioClient;

  private final CustomResourceRepository<Scenario> scenarioRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  private final List<ExecutorStep<?>> preparationSteps;

  private final List<ExecutorStep<?>> postScenarioExecutionSteps;

  private final List<ExecutorStep<?>> postExecutionSteps;

  public IstioScenarioExecutor(KubernetesClient kubernetesClient, CustomResourceRepository<ExecutionQueue> queueCustomResourceRepository, CustomResourceRepository<Scenario> scenarioRepository, CustomResourceRepository<ExecutionQueue> executionRepository) {
    this.kubernetesClient = kubernetesClient;
    istioClient = new DefaultIstioClient(kubernetesClient);
    this.scenarioRepository = scenarioRepository;
    this.executionRepository = executionRepository;

    preparationSteps = List.of(
            new UpdateQueueStep(kubernetesClient),
            new IstioRetryStep(kubernetesClient, istioClient),
            // new IstioCircuitBreakerStep(kubernetesClient, istioClient),
            new IstioFaultStep(kubernetesClient, istioClient)
    );
    postScenarioExecutionSteps = List.of(new UpdateQueueStep(kubernetesClient));
    postExecutionSteps = List.of();
  }

  public IstioScenarioExecutor(KubernetesClient kubernetesClient) {
    this(
            kubernetesClient,
            new CustomResourceRepository<>(kubernetesClient.resources(ExecutionQueue.class)),
            new CustomResourceRepository<>(kubernetesClient.resources(Scenario.class)),
            new CustomResourceRepository<>(kubernetesClient.resources(ExecutionQueue.class)));
  }

  private Optional<Item> getNextItem(ExecutionQueue queue) {
    var items = queue.getSpec().getItems();
    return items.stream().filter(item -> !item.isFinished()).findFirst();
  }

  public void run(ExecutionQueue queue) {
    var nextItem = getNextItem(queue);

    if (nextItem.isPresent()) {
      var namespace = queue.getMetadata().getNamespace();
      if (nextItem.get().isPending()) {
        if (!existsJobRunning(namespace)) {
          runScenario(namespace, nextItem.get().getScenario());
        }
      }
    } else {
      logger.debug("No queue item present for: {}", queue.getMetadata().getName());
      if (isAllFinished(queue)) {
        logger.debug("All items finished for: {}", queue.getMetadata().getName());

        // collect all results and send to S3
        var scenarios = queue.getSpec().getItems().stream().map(Item::getScenario).toList();

      }
    }
  }

  public boolean isAllFinished(ExecutionQueue queue) {
    return queue.getSpec().getItems().stream().allMatch(Item::isFinished);
  }

  private boolean existsJobRunning(String namespace) {
    var jobs = kubernetesClient.batch().v1().jobs().inNamespace(namespace).list();
    return jobs.getItems().stream().anyMatch(job ->
            isNull(job.getStatus().getCompletionTime()) && job.getMetadata().getAnnotations().containsKey("resiliencebench.io/scenario"));
  }

  private Job startLoadGeneration(Scenario scenario) {
    var loadGeneratorStep = new K6LoadGeneratorStep(kubernetesClient);
    return loadGeneratorStep.execute(scenario);
  }

  private void runScenario(String namespace, String name) {
    logger.debug("Running scenario: {}", name);
    var scenarioRepository = new CustomResourceRepository<>(kubernetesClient, Scenario.class);
    var scenario = scenarioRepository.get(namespace, name);
    if (scenario.isPresent()) {
      preparationSteps.forEach(step -> step.execute(scenario.get()));
      var job = startLoadGeneration(scenario.get());
      var jobsClient = kubernetesClient.batch().v1().jobs();
      job = jobsClient.resource(job).create();
      jobsClient.resource(job).watch(this);
      logger.debug("Job created: {}", job.getMetadata().getName());
    } else {
      throw new RuntimeException(format("Scenario not found: %s.%s", namespace, name));
    }
  }

  @Override
  public void eventReceived(Action action, Job resource) { // TODO precisa melhorar esse método. mto emaranhado!
    var namespace = resource.getMetadata().getNamespace();
    if (action.equals(Action.MODIFIED)) {
      if (nonNull(resource.getStatus().getCompletionTime())) {
        logger.debug("Finished job: {}", resource.getMetadata().getName());

        var scenarioName = resource.getMetadata().getAnnotations().get("resiliencebench.io/scenario");
        var scenario = scenarioRepository.get(namespace, scenarioName).get();

        postScenarioExecutionSteps.forEach(step -> step.execute(scenario));

        var executionQueue = executionRepository.get(namespace, scenario.getMetadata().getAnnotations().get(OWNED_BY)).get();
        run(executionQueue);
      }
    }
  }

  @Override
  public void onClose(WatcherException cause) {
    // TODO o que acontece pra cair aqui?
  }
}
