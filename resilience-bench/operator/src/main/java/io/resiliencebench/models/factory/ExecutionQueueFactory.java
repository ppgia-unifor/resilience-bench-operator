package io.resiliencebench.models.factory;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.resiliencebench.models.benchmark.Benchmark;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.queue.ExecutionQueueSpec;
import io.resiliencebench.models.queue.QueueItem;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.support.Annotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Factory class for creating ExecutionQueue instances.
 * This class provides methods to create execution queues based on benchmarks and scenarios.
 */
@Component
public class ExecutionQueueFactory {

  private static String resultFilePathTemplate;

  /**
   * Constructs an ExecutionQueueFactory with the specified result file path template.
   * This constructor is used by Spring to inject the configuration property.
   *
   * @param resultFilePathTemplate the template for the result file paths, injected from the application properties
   */
  public ExecutionQueueFactory(@Value("${resiliencebench.resultFilePathTemplate}") String resultFilePathTemplate) {
    ExecutionQueueFactory.resultFilePathTemplate = resultFilePathTemplate;
  }

  /**
   * Creates an ExecutionQueue for the given benchmark and list of scenarios.
   * The method generates the metadata and specifications needed to initialize an execution queue.
   *
   * @param benchmark the benchmark for which the execution queue is to be created
   * @param scenarios the list of scenarios to be included in the execution queue
   * @return a new instance of ExecutionQueue initialized with the provided benchmark and scenarios
   */
  public static ExecutionQueue create(Benchmark benchmark, List<Scenario> scenarios) {
    ObjectMeta metadata = buildMetadata(benchmark);
    List<QueueItem> items = buildQueueItems(scenarios);

    ExecutionQueueSpec spec = new ExecutionQueueSpec(
            String.format(resultFilePathTemplate, UUID.randomUUID().toString()),
            items,
            benchmark.getMetadata().getNamespace()
    );

    return new ExecutionQueue(spec, metadata);
  }

  /**
   * Builds the metadata for the execution queue based on the benchmark.
   *
   * @param benchmark the benchmark from which to extract metadata
   * @return the constructed ObjectMeta instance
   */
  private static ObjectMeta buildMetadata(Benchmark benchmark) {
    return new ObjectMetaBuilder()
            .withNamespace(benchmark.getMetadata().getNamespace())
            .addToAnnotations(Annotations.OWNED_BY, benchmark.getMetadata().getNamespace())
            .withName(benchmark.getMetadata().getName())
            .build();
  }

  /**
   * Builds a list of QueueItem instances from the provided list of scenarios.
   *
   * @param scenarios the list of scenarios to be converted into QueueItems
   * @return a list of QueueItem instances
   */
  private static List<QueueItem> buildQueueItems(List<Scenario> scenarios) {
    return scenarios.stream()
            .map(scenario -> new QueueItem(scenario.getMetadata().getName()))
            .collect(Collectors.toList());
  }
}
