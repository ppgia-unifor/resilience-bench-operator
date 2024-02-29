package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.execution.Scheduler;
import br.unifor.ppgia.resiliencebench.resources.ExecutionQueueFactory;
import br.unifor.ppgia.resiliencebench.resources.ScenarioFactory;
import br.unifor.ppgia.resiliencebench.resources.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.fabric8.kubernetes.client.KubernetesClient;
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

  private final KubernetesClient kubernetesClient;

  private final CustomResourceRepository<Scenario> scenarioRepository;

  private final CustomResourceRepository<Workload> workloadRepository;
  private final CustomResourceRepository<ExecutionQueue> executionRepository;

  private final Scheduler scheduler;

  public BenchmarkReconciler(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
    scenarioRepository = new CustomResourceRepository<>(kubernetesClient.resources(Scenario.class));
    workloadRepository = new CustomResourceRepository<>(kubernetesClient.resources(Workload.class));
    executionRepository = new CustomResourceRepository<>(kubernetesClient.resources(ExecutionQueue.class));
    scheduler = new Scheduler(kubernetesClient);
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
