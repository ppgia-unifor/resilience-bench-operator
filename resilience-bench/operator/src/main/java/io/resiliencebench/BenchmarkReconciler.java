package io.resiliencebench;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.resiliencebench.execution.ScenarioExecutor;
import io.resiliencebench.resources.ExecutionQueueFactory;
import io.resiliencebench.resources.ScenarioFactory;
import io.resiliencebench.resources.benchmark.Benchmark;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.workload.Workload;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ControllerConfiguration
public class BenchmarkReconciler implements Reconciler<Benchmark> {

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkReconciler.class);

  private final CustomResourceRepository<Scenario> scenarioRepository;

  private final CustomResourceRepository<Workload> workloadRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  private final ScenarioExecutor scenarioExecutor;

  public BenchmarkReconciler(ScenarioExecutor scenarioExecutor,
                             CustomResourceRepository<Scenario> scenarioRepository,
                             CustomResourceRepository<Workload> workloadRepository,
                             CustomResourceRepository<ExecutionQueue> executionRepository) {
    this.scenarioExecutor = scenarioExecutor;
    this.scenarioRepository = scenarioRepository;
    this.workloadRepository = workloadRepository;
    this.executionRepository = executionRepository;
  }

  @Override
  public UpdateControl<Benchmark> reconcile(Benchmark benchmark, Context<Benchmark> context) {
    var workload = workloadRepository.get(benchmark.getMetadata().getNamespace(), benchmark.getSpec().getWorkload());
    if (workload.isEmpty()) {
      logger.error("Workload not found: {}", benchmark.getSpec().getWorkload());
      return UpdateControl.noUpdate();
    }

    var scenariosList = ScenarioFactory.create(benchmark, workload.get());
    if (scenariosList.isEmpty()) {
      logger.error("No scenarios found for workload: {}", benchmark.getSpec().getWorkload());
      return UpdateControl.noUpdate();
    }

    var executionQueue = getOrCreateQueue(benchmark, executionRepository, scenariosList);
    scenariosList.forEach(scenario -> createOrUpdateScenario(scenario, scenarioRepository));

    scenarioExecutor.run(executionQueue);
    logger.info("Benchmark reconciled: {}", benchmark.getMetadata().getName());
    return UpdateControl.noUpdate();
  }

  private ExecutionQueue getOrCreateQueue(Benchmark benchmark, CustomResourceRepository<ExecutionQueue> executionRepository, List<Scenario> scenariosList) {
    var queue = executionRepository.get(benchmark.getMetadata().getNamespace(), benchmark.getMetadata().getName());
    if (queue.isPresent()) {
      logger.debug("ExecutionQueue already exists: {}", benchmark.getMetadata().getName());
      return queue.get();
    } else {
      var queueCreated = ExecutionQueueFactory.create(benchmark, scenariosList);
      executionRepository.create(queueCreated);
      return queueCreated;
    }
  }

  private void createOrUpdateScenario(Scenario scenario, CustomResourceRepository<Scenario> scenarioRepository) {
    var foundScenario = scenarioRepository.get(scenario.getMetadata());
    if (foundScenario.isEmpty()) {
      scenarioRepository.create(scenario);
    } else {
      logger.debug("Scenario already exists: {}", scenario.getMetadata().getName());
    }
  }
}
