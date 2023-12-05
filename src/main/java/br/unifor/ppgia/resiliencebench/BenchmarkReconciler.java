package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.CustomResourceRepository;
import br.unifor.ppgia.resiliencebench.resources.ScenarioFactory;
import br.unifor.ppgia.resiliencebench.resources.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;

import java.util.Map;

import static java.util.Map.*;

@ControllerConfiguration
public class BenchmarkReconciler implements Reconciler<Benchmark> {

  @Override
  public UpdateControl<Benchmark> reconcile(Benchmark benchmark, Context<Benchmark> context) {
    var scenarioRepository = new CustomResourceRepository<>(context.getClient(), Scenario.class);

    var workload = context.getClient().resources(Workload.class).inNamespace(benchmark.getMetadata().getNamespace()).withName(benchmark.getSpec().getWorkload()).get();
    var scenariosList = ScenarioFactory.create(benchmark, workload);

    for (var scenario : scenariosList) {
      var meta = new ObjectMeta();
      meta.setName(scenario.toString());
      meta.setNamespace(benchmark.getMetadata().getNamespace());
      meta.setAnnotations(of("resiliencebench.io/owned-by", benchmark.getMetadata().getName()));
      scenario.setMetadata(meta);
      var foundScenario = scenarioRepository.get(meta);
      if (foundScenario != null) {
        scenarioRepository.update(scenario);
      } else {
        scenarioRepository.create(scenario);
      }

    }

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
