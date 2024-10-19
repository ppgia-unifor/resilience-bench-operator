package io.resiliencebench.resources;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.resiliencebench.resources.benchmark.Benchmark;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.queue.ExecutionQueueSpec;
import io.resiliencebench.resources.queue.ExecutionQueueItem;
import io.resiliencebench.resources.scenario.Scenario;
import static io.resiliencebench.support.Annotations.OWNED_BY;

public class ExecutionQueueFactory {

  public ExecutionQueueFactory() {
    throw new IllegalStateException("Utility class");
  }

  public static ExecutionQueue create(Benchmark benchmark, List<Scenario> scenarios) {
    var meta = new ObjectMetaBuilder()
            .withNamespace(benchmark.getMetadata().getNamespace())
            .addToAnnotations(OWNED_BY, benchmark.getMetadata().getNamespace())
            .withName(benchmark.getMetadata().getName())
            .build();

    var resultsFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    var finalResultsFile = "%s/results.json".formatted(LocalDateTime.now().toString());
    var itemResultsFile = resultsFolder + "/%s.json";

    var items = scenarios.stream().map(s -> new ExecutionQueueItem(
            s.getMetadata().getName(), itemResultsFile.formatted(s.getMetadata().getName()))
    );
    var spec = new ExecutionQueueSpec(
            finalResultsFile,
            items.toList(),
            benchmark.getMetadata().getNamespace()
    );
    return new ExecutionQueue(spec, meta);
  }
}
