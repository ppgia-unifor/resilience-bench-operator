package io.resiliencebench.execution.istio;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.resiliencebench.execution.ExecutorStep;
import io.resiliencebench.execution.ResultFileStep;
import io.resiliencebench.execution.ScenarioExecutor;
import io.resiliencebench.execution.UpdateStatusQueueStep;
import io.resiliencebench.execution.istio.steps.IstioCircuitBreakerStep;
import io.resiliencebench.execution.istio.steps.IstioFaultStep;
import io.resiliencebench.execution.istio.steps.IstioRetryStep;
import io.resiliencebench.execution.k6.K6LoadGeneratorStep;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.queue.Item;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.support.Annotations;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class IstioScenarioExecutor implements Watcher<Job>, ScenarioExecutor {

  private final static Logger logger = LoggerFactory.getLogger(IstioScenarioExecutor.class);
  private final KubernetesClient kubernetesClient;

  private final CustomResourceRepository<Scenario> scenarioRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  private final List<ExecutorStep<?>> preparationSteps;

  private final List<ExecutorStep<?>> postScenarioExecutionSteps;
  private final K6LoadGeneratorStep k6LoadGeneratorStep;

  public IstioScenarioExecutor(
          KubernetesClient kubernetesClient,
          CustomResourceRepository<Scenario> scenarioRepository,
          CustomResourceRepository<ExecutionQueue> executionRepository,
          UpdateStatusQueueStep updateStatusQueueStep,
          IstioCircuitBreakerStep istioCircuitBreakerStep,
          ResultFileStep resultFileStep,
          IstioRetryStep istioRetryStep,
          IstioFaultStep istioFaultStep,
          K6LoadGeneratorStep k6LoadGeneratorStep) {
    this.kubernetesClient = kubernetesClient;
    this.scenarioRepository = scenarioRepository;
    this.executionRepository = executionRepository;
    this.k6LoadGeneratorStep = k6LoadGeneratorStep;

    preparationSteps = List.of(updateStatusQueueStep, istioRetryStep, istioCircuitBreakerStep, istioFaultStep);
    postScenarioExecutionSteps = List.of(updateStatusQueueStep, resultFileStep);
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

  private Job startLoadGeneration(Scenario scenario, ExecutionQueue executionQueue) {
    return k6LoadGeneratorStep.execute(scenario, executionQueue);
  }

  private void runScenario(String namespace, String scenarioName, ExecutionQueue executionQueue) {
    logger.info("Running scenario: {}", scenarioName);
    var scenario = scenarioRepository.find(namespace, scenarioName);
    if (scenario.isPresent()) {
      preparationSteps.forEach(step -> step.execute(scenario.get(), executionQueue));
      var job = startLoadGeneration(scenario.get(), executionQueue);
      var jobsClient = kubernetesClient.batch().v1().jobs();
      job = jobsClient.resource(job).create();
      jobsClient.resource(job).watch(this);
      logger.info("Job created: {}", job.getMetadata().getName());
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
        postScenarioExecutionSteps.forEach(step -> step.execute(scenario, executionQueue));
        run(executionQueue);
      }
    }
  }

  @Override
  public void onClose(WatcherException cause) {
    // TODO o que acontece pra cair aqui?
  }
}
