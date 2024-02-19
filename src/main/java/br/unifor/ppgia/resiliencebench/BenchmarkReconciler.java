package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.execution.ExecutionQueueFactory;
import br.unifor.ppgia.resiliencebench.execution.ScenarioFactory;
import br.unifor.ppgia.resiliencebench.execution.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.modeling.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.modeling.workload.Workload;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerConfiguration
public class BenchmarkReconciler implements Reconciler<Benchmark> {

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkReconciler.class);

  @Override
  public UpdateControl<Benchmark> reconcile(Benchmark benchmark, Context<Benchmark> context) {
    var scenarioRepository = new CustomResourceRepository<>(context.getClient(), Scenario.class);
    var workloadRepository = new CustomResourceRepository<>(context.getClient(), Workload.class);
    var executionRepository = new CustomResourceRepository<>(context.getClient(), ExecutionQueue.class);

    var workload = workloadRepository.get(benchmark.getMetadata().getNamespace(), benchmark.getSpec().getWorkload());
    if (workload.isEmpty()) {
      logger.warn("Workload not found: {}", benchmark.getSpec().getWorkload());
      return UpdateControl.noUpdate();
    }

    var scenariosList = ScenarioFactory.create(benchmark, workload.get()); // TODO handle empty list

    var executionQueueOpt = executionRepository.get(benchmark.getMetadata().getNamespace(), benchmark.getMetadata().getName());
    ExecutionQueue executionQueue = null;
    if (executionQueueOpt.isPresent()) {
      logger.debug("ExecutionQueue already exists: {}", benchmark.getMetadata().getName());
      executionQueue = executionQueueOpt.get();
    } else {
      executionQueue = ExecutionQueueFactory.create(benchmark, scenariosList);
      executionRepository.create(executionQueue);
    }

    scenariosList.forEach(scenario -> createOrUpdateScenario(scenario, scenarioRepository));

    var scheduler = new Scheduler(context.getClient());
    scheduler.run(executionQueue);

    logger.info("Benchmark reconciled: {}", benchmark.getMetadata().getName());
    return UpdateControl.noUpdate();
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
