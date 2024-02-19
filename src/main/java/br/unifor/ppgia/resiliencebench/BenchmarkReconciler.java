package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.CustomResourceRepository;
import br.unifor.ppgia.resiliencebench.resources.ScenarioFactory;
import br.unifor.ppgia.resiliencebench.resources.modeling.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.modeling.benchmark.BenchmarkStatus;
import br.unifor.ppgia.resiliencebench.resources.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.Workload;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
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

    var workload = workloadRepository.get(benchmark.getMetadata().getNamespace(), benchmark.getSpec().getWorkload());
    if (workload.isEmpty()) {
      logger.warn("Workload not found: {}", benchmark.getSpec().getWorkload());
      return UpdateControl.noUpdate();
    }
    var scenariosList = ScenarioFactory.create(benchmark, workload.get());
    scenariosList.forEach(scenario -> createOrUpdateScenario(benchmark, scenario, scenarioRepository));
    var status = new BenchmarkStatus(scenariosList.size(), 0);
    benchmark.setStatus(status);
    return UpdateControl.updateStatus(benchmark);
  }

  private void createOrUpdateScenario(Benchmark benchmark, Scenario scenario, CustomResourceRepository<Scenario> scenarioRepository) {
    scenario.setMetadata(new ObjectMetaBuilder()
            .withName(scenario.toString())
            .withNamespace(benchmark.getMetadata().getNamespace())
            .addToAnnotations("resiliencebench.io/owned-by", benchmark.getMetadata().getName())
            .addToAnnotations("resiliencebench.io/scenario-uid", scenario.toString())
            .build());
    var foundScenario = scenarioRepository.get(scenario.getMetadata());
    if (foundScenario.isEmpty()) {
      scenarioRepository.create(scenario);
    } else {
      logger.debug("Scenario already exists: {}", scenario.getMetadata().getName());
    }
  }
}
