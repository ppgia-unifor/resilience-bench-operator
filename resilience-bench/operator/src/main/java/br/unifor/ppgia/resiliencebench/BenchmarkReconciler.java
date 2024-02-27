package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.ExecutionQueueFactory;
import br.unifor.ppgia.resiliencebench.resources.ScenarioFactory;
import br.unifor.ppgia.resiliencebench.resources.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ControllerConfiguration
public class BenchmarkReconciler implements Reconciler<Benchmark> {

  private static final Logger logger = LoggerFactory.getLogger(BenchmarkReconciler.class);

  @Override
  public UpdateControl<Benchmark> reconcile(Benchmark benchmark, Context<Benchmark> context) {
    var scenarioRepository = new CustomResourceRepository<>(context.getClient(), Scenario.class);
    var workloadRepository = new CustomResourceRepository<>(context.getClient(), Workload.class);

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

    var executionRepository = new CustomResourceRepository<>(context.getClient(), ExecutionQueue.class);
    var executionQueue = getOrCreateQueue(benchmark, executionRepository, scenariosList);

    scenariosList.forEach(scenario -> createOrUpdateScenario(scenario, scenarioRepository));

    var scheduler = new Scheduler(context.getClient());
    scheduler.run(executionQueue);

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
