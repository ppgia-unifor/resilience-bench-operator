
package io.resiliencebench.execution;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.resiliencebench.execution.steps.StepRegister;
import io.resiliencebench.execution.steps.k6.K6LoadGeneratorStep;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.queue.Item;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.support.Annotations;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class ScenarioExecutor implements Watcher<Job> {

  private final static Logger logger = LoggerFactory.getLogger(ScenarioExecutor.class);
  private final KubernetesClient kubernetesClient;

  private final CustomResourceRepository<Scenario> scenarioRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  private final StepRegister stepRegister;

  private final K6LoadGeneratorStep k6LoadGeneratorStep;

  public ScenarioExecutor(
          KubernetesClient kubernetesClient,
          StepRegister stepRegister,
          CustomResourceRepository<Scenario> scenarioRepository,
          CustomResourceRepository<ExecutionQueue> executionRepository,
          K6LoadGeneratorStep k6LoadGeneratorStep) {
    this.kubernetesClient = kubernetesClient;
    this.stepRegister = stepRegister;
    this.scenarioRepository = scenarioRepository;
    this.executionRepository = executionRepository;
    this.k6LoadGeneratorStep = k6LoadGeneratorStep;
  }

  private Optional<Item> getNextItem(ExecutionQueue queue) {
    var items = queue.getSpec().getItems();
    return items.stream().filter(Item::isPending).findFirst();
  }

  public void run(ExecutionQueue queue) {
    var queueToExecute = executionRepository.find(queue.getMetadata())
            .orElseThrow(() -> new RuntimeException("Queue not found " + queue.getMetadata().getName()));

    var nextItem = getNextItem(queueToExecute);

    if (nextItem.isPresent()) {
      var namespace = queueToExecute.getMetadata().getNamespace();
      if (nextItem.get().isPending()) {
        if (!isRunning(namespace)) {
          runScenario(namespace, nextItem.get().getScenario(), queueToExecute);
        }
      }
    } else {
      logger.info("No item available for queue: {}", queueToExecute.getMetadata().getName());
      if (isAllFinished(queueToExecute)) {
        logger.info("All items finished for: {}", queueToExecute.getMetadata().getName());
      }
    }
  }

  public boolean isAllFinished(ExecutionQueue queue) {
    return queue.getSpec().getItems().stream().allMatch(Item::isFinished);
  }

  private boolean isRunning(String namespace) {
    var jobs = kubernetesClient.batch().v1().jobs().inNamespace(namespace).list();
    return jobs.getItems().stream().anyMatch(job ->
            isNull(job.getStatus().getCompletionTime()) && job.getMetadata().getAnnotations().containsKey("resiliencebench.io/scenario"));
  }

  private Job createLoadGenerationJob(Scenario scenario, ExecutionQueue executionQueue) {
    return k6LoadGeneratorStep.execute(scenario, executionQueue);
  }

  private void runJob(Job job) {
    var jobsClient = kubernetesClient.batch().v1().jobs();
    var createdJob =
            jobsClient.inNamespace(job.getMetadata().getNamespace()).withName(job.getMetadata().getName()).get();
    if (createdJob != null) {
      jobsClient.resource(createdJob).delete();
    }
    jobsClient.resource(job).create();
    jobsClient.resource(job).watch(this);
    logger.info("Job created: {}", job.getMetadata().getName());
  }

  private void runPreparationSteps(Scenario scenario, ExecutionQueue executionQueue) {
    stepRegister.getPreparationSteps().forEach(step -> step.execute(scenario, executionQueue));
  }

  private void runScenario(String namespace, String scenarioName, ExecutionQueue executionQueue) {
    logger.info("Running scenario: {}", scenarioName);
    var scenario = scenarioRepository.find(namespace, scenarioName);
    if (scenario.isPresent()) {
      runPreparationSteps(scenario.get(), executionQueue);
      var job = createLoadGenerationJob(scenario.get(), executionQueue);
      runJob(job);
    } else {
      throw new RuntimeException(format("Scenario not found: %s.%s", namespace, scenarioName));
    }
  }

  @Override
  public void eventReceived(Action action, Job resource) { // TODO precisa melhorar esse método. mto emaranhado!
    var namespace = resource.getMetadata().getNamespace();
    if (action.equals(Action.MODIFIED)) {
      if (nonNull(resource.getStatus().getCompletionTime())) {
        logger.debug("Finished job: {}", resource.getMetadata().getName());
        var scenarioName = resource.getMetadata().getAnnotations().get("resiliencebench.io/scenario");
        var scenario = scenarioRepository.get(namespace, scenarioName);
        var executionQueue = executionRepository.get(
                namespace,
                scenario.getMetadata().getAnnotations().get(Annotations.OWNED_BY)
        );
        stepRegister.getPostExecutionSteps().forEach(step -> step.execute(scenario, executionQueue));
        run(executionQueue);
      }
    }
  }

  @Override
  public void onClose(WatcherException cause) {
    // TODO o que acontece pra cair aqui?
  }
}
