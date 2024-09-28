
package io.resiliencebench.execution;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.support.CustomResourceRepository;

@Service
public class DefaultQueueExecutor implements QueueExecutor {

  private final static Logger logger = LoggerFactory.getLogger(DefaultQueueExecutor.class);

  private final CustomResourceRepository<Scenario> scenarioRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  private final ScenarioExecutor scenarioExecutor;

  public DefaultQueueExecutor(
          CustomResourceRepository<Scenario> scenarioRepository,
          CustomResourceRepository<ExecutionQueue> executionRepository,
          ScenarioExecutor scenarioExecutor) {
    this.scenarioRepository = scenarioRepository;
    this.executionRepository = executionRepository;
    this.scenarioExecutor = scenarioExecutor;
  }

  @Override
  public void execute(ExecutionQueue queue) {
    var queueToExecute = executionRepository.find(queue.getMetadata())
            .orElseThrow(() -> new RuntimeException("Queue not found " + queue.getMetadata().getName()));

    var nextItem = queueToExecute.getNextPendingItem();

    if (nextItem.isPresent()) {
      var namespace = queueToExecute.getMetadata().getNamespace();
      if (nextItem.get().isPending()) {
        executeScenario(namespace, nextItem.get().getScenario(), queueToExecute);
      }
    } else {
      logger.info("No item available for queue: {}", queueToExecute.getMetadata().getName());
      if (queueToExecute.isDone()) {
        logger.info("All items finished for: {}", queueToExecute.getMetadata().getName());
      }
    }
  }

  private void executeScenario(String namespace, String scenarioName, ExecutionQueue executionQueue) {
    logger.info("Running scenario: {}", scenarioName);
    var scenario = scenarioRepository.find(namespace, scenarioName);
    if (scenario.isPresent()) {
      scenarioExecutor.execute(scenario.get(), executionQueue, () -> this.execute(executionQueue));
    } else {
      throw new RuntimeException(format("Scenario not found: %s.%s", namespace, scenarioName));
    }
  }
}
