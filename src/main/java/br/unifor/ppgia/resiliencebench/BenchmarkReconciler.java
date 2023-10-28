package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.benchmark.Benchmark;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

@ControllerConfiguration
public class BenchmarkReconciler implements Reconciler<Benchmark> {

  @Override
  public UpdateControl<Benchmark> reconcile(Benchmark benchmark, Context<Benchmark> context) {
//    var scenariosList = ScenarioFactory.create(benchmark);
//    var status = new BenchmarkStatus(scenariosList.size());
//    benchmark.setStatus(status);

//    var scenarioOperations = context.getClient().resources(Scenario.class);
//    scenariosList.forEach(scenario -> scenarioOperations.inNamespace(benchmark.getMetadata().getNamespace()).resource(scenario).create());

//    for (var scenario : scenariosList) {
//      scenario.getMetadata().setName(scenario.getSpec().getId());
//      scenarioOperations.inNamespace(benchmark.getMetadata().getNamespace()).resource(scenario).create();
//    }
//    return UpdateControl.updateStatus(benchmark);
    return UpdateControl.noUpdate();
  }
}
