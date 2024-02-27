package br.unifor.ppgia.resiliencebench.resources;

import br.unifor.ppgia.resiliencebench.resources.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.resources.queue.ExecutionQueueSpec;
import br.unifor.ppgia.resiliencebench.resources.queue.Item;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;

import java.util.List;

import static br.unifor.ppgia.resiliencebench.support.Annotations.OWNED_BY;

public class ExecutionQueueFactory {

  public static ExecutionQueue create(Benchmark benchmark, List<Scenario> scenarios) {
    var meta = new ObjectMetaBuilder()
            .withNamespace(benchmark.getMetadata().getNamespace())
            .addToAnnotations(OWNED_BY, benchmark.getMetadata().getNamespace())
            .withName(benchmark.getMetadata().getName())
            .build();

    var items = scenarios.stream().map(s -> new Item(s.getMetadata().getName()));
    var spec = new ExecutionQueueSpec();
    spec.setItems(items.toList());

    return new ExecutionQueue(spec, meta);
  }
}
