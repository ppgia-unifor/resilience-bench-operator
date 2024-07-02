package io.resiliencebench.controllers;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.resiliencebench.execution.steps.model.ScenarioExecutor;
import io.resiliencebench.models.enums.BenchmarkReconcilerMessages;
import io.resiliencebench.models.enums.ScenarioStatusEnum;
import io.resiliencebench.models.factory.ExecutionQueueFactory;
import io.resiliencebench.models.factory.ScenarioFactory;
import io.resiliencebench.models.benchmark.Benchmark;
import io.resiliencebench.models.benchmark.BenchmarkStatus;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.models.scenario.ScenarioStatus;
import io.resiliencebench.models.service.ResilientService;
import io.resiliencebench.models.workload.Workload;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * BenchmarkReconciler is responsible for reconciling Benchmark custom resources,
 * ensuring they are up-to-date and initiating scenario execution.
 */
@Component
@ControllerConfiguration
public class BenchmarkReconciler implements Reconciler<Benchmark> {

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkReconciler.class);

  private final ScenarioExecutor scenarioExecutor;
  private final CustomResourceRepository<Scenario> scenarioRepository;
  private final CustomResourceRepository<Workload> workloadRepository;
  private final CustomResourceRepository<ExecutionQueue> queueRepository;
  private final CustomResourceRepository<ResilientService> resilientServiceRepository;

  @Value("${resiliencebench.resilientServiceName}")
  private String resilientServiceName;

  /**
   * Constructs a new BenchmarkReconciler.
   *
   * @param scenarioExecutor            the executor responsible for running scenarios
   * @param scenarioRepository          repository for Scenario custom resources
   * @param workloadRepository          repository for Workload custom resources
   * @param queueRepository             repository for ExecutionQueue custom resources
   * @param resilientServiceRepository  repository for ResilientService custom resources
   */
  public BenchmarkReconciler(final ScenarioExecutor scenarioExecutor,
          final CustomResourceRepository<Scenario> scenarioRepository,
          final CustomResourceRepository<Workload> workloadRepository,
          final CustomResourceRepository<ExecutionQueue> queueRepository,
          final CustomResourceRepository<ResilientService> resilientServiceRepository) {
    this.scenarioExecutor = scenarioExecutor;
    this.scenarioRepository = scenarioRepository;
    this.workloadRepository = workloadRepository;
    this.queueRepository = queueRepository;
    this.resilientServiceRepository = resilientServiceRepository;
  }

  /**
   * Reconciles the specified Benchmark resource.
   *
   * @param benchmark the Benchmark resource to reconcile
   * @param context   the context of the reconciliation
   * @return an UpdateControl object indicating the outcome of the reconciliation
   */
  @Override
  public UpdateControl<Benchmark> reconcile(final Benchmark benchmark, final Context<Benchmark> context) {
    logger.debug(BenchmarkReconcilerMessages.SCENARIO_RECONCILIATION_STARTED.format(benchmark.getMetadata().getName()));

    final Optional<Workload> workloadOpt = workloadRepository.find(benchmark.getMetadata().getNamespace(), benchmark.getSpec().getWorkload());

    if (workloadOpt.isEmpty()) {
      logger.error(BenchmarkReconcilerMessages.WORKLOAD_NOT_FOUND.format(benchmark.getSpec().getWorkload()));
      return UpdateControl.noUpdate();
    }

    final List<Scenario> scenarios = ScenarioFactory.create(benchmark, workloadOpt.get());
    if (scenarios.isEmpty()) {
      logger.error(BenchmarkReconcilerMessages.NO_SCENARIOS_GENERATED.format(benchmark.getMetadata().getName()));
      return UpdateControl.noUpdate();
    }

    cleanPreviousResources(benchmark);

    final ExecutionQueue executionQueue = getOrCreateQueue(benchmark, scenarios);
    scenarios.forEach(scenario -> {
      scenario.setStatus(new ScenarioStatus(ScenarioStatusEnum.PENDING));
      createOrUpdateScenario(scenario);
    });

    logger.debug(BenchmarkReconcilerMessages.SCENARIO_EXECUTION_STARTED.format(benchmark.getMetadata().getName()));
    scenarioExecutor.run(executionQueue);

    logger.info(BenchmarkReconcilerMessages.BENCHMARK_RECONCILED.format(benchmark.getMetadata().getName(), scenarios.size()));

    benchmark.setStatus(new BenchmarkStatus(scenarios.size()));
    updateResilientServiceStatus(benchmark.getMetadata().getNamespace(), ScenarioStatusEnum.COMPLETED.getStatus(), ScenarioStatusEnum.COMPLETED.getMessage());
    return UpdateControl.updateStatus(benchmark);
  }

  /**
   * Cleans up previous resources associated with the specified benchmark.
   *
   * @param benchmark the benchmark whose associated resources are to be cleaned up
   */
  private void cleanPreviousResources(final Benchmark benchmark) {
    logger.debug(BenchmarkReconcilerMessages.CLEANING_PREVIOUS_RESOURCES.format(benchmark.getMetadata().getName()));

    final String namespace = benchmark.getMetadata().getNamespace();
    queueRepository.deleteAll(namespace);
    scenarioRepository.deleteAll(namespace);
  }

  /**
   * Retrieves or creates a new ExecutionQueue for the specified benchmark.
   *
   * @param benchmark   the benchmark for which to retrieve or create an ExecutionQueue
   * @param scenarios   the list of scenarios to include in the ExecutionQueue
   * @return the existing or newly created ExecutionQueue
   */
  private ExecutionQueue getOrCreateQueue(final Benchmark benchmark, final List<Scenario> scenarios) {
    final Optional<ExecutionQueue> queueOpt = queueRepository.find(benchmark.getMetadata().getNamespace(), benchmark.getMetadata().getName());

    return queueOpt.orElseGet(() -> {
      final ExecutionQueue newQueue = ExecutionQueueFactory.create(benchmark, scenarios);
      queueRepository.create(newQueue);
      logger.debug(BenchmarkReconcilerMessages.CREATED_NEW_EXECUTION_QUEUE.format(benchmark.getMetadata().getName()));
      return newQueue;
    });
  }

  /**
   * Creates or updates the specified scenario.
   *
   * @param scenario the scenario to create or update
   */
  private void createOrUpdateScenario(final Scenario scenario) {
    final Optional<Scenario> foundScenarioOpt = scenarioRepository.find(scenario.getMetadata());

    if (foundScenarioOpt.isEmpty()) {
      scenarioRepository.create(scenario);
      logger.debug(BenchmarkReconcilerMessages.SCENARIO_CREATED.format(scenario.getMetadata().getName()));
    } else {
      logger.debug(BenchmarkReconcilerMessages.SCENARIO_EXISTS.format(scenario.getMetadata().getName()));
    }
  }

  /**
   * Updates the status of the ResilientService.
   *
   * @param namespace the namespace of the ResilientService
   * @param status    the new status to set
   * @param message   the message related to the status
   */
  private void updateResilientServiceStatus(String namespace, String status, String message) {
    final Optional<ResilientService> resilientServiceOpt = resilientServiceRepository.find(namespace, resilientServiceName);

    resilientServiceOpt.ifPresent(resilientService -> {
      resilientService.getStatus().setStatus(status);
      resilientService.getStatus().setMessage(message);
      resilientServiceRepository.updateStatus(resilientService);
      logger.info(BenchmarkReconcilerMessages.UPDATED_RESILIENT_SERVICE_STATUS.format(status, message));
    });
  }
}
