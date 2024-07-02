package io.resiliencebench.models.factory;

import static io.resiliencebench.support.Annotations.OWNED_BY;

import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.resiliencebench.models.benchmark.Benchmark;
import io.resiliencebench.models.benchmark.ConnectorTemplate;
import io.resiliencebench.models.benchmark.ScenarioTemplate;
import io.resiliencebench.models.benchmark.SourceTemplate;
import io.resiliencebench.models.scenario.*;
import io.resiliencebench.models.utils.ListExpansion;
import io.resiliencebench.models.workload.Workload;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory class for creating Scenario instances.
 * This class provides methods to create scenarios based on benchmarks and workloads.
 */
public final class ScenarioFactory {

  /**
   * Private constructor to prevent instantiation.
   */
  private ScenarioFactory() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Converts a JsonNode to a Map.
   *
   * @param jsonNode the JsonNode to convert
   * @return a Map representing the JsonNode
   */
  private static Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
    if (jsonNode == null || !jsonNode.isObject()) {
      return Collections.emptyMap();
    }
    Map<String, Object> resultMap = new HashMap<>();
    jsonNode.fields().forEachRemaining(entry -> resultMap.put(entry.getKey(), convertJsonNode(entry.getValue())));
    return resultMap;
  }

  /**
   * Converts a JsonNode to an appropriate Java object.
   *
   * @param jsonNode the JsonNode to convert
   * @return the Java object representing the JsonNode
   */
  public static Object convertJsonNode(JsonNode jsonNode) {
    if (jsonNode.isObject()) {
      return convertJsonNodeToMap(jsonNode);
    } else if (jsonNode.isArray()) {
      List<Object> list = new ArrayList<>();
      jsonNode.elements().forEachRemaining(element -> list.add(convertJsonNode(element)));
      return list;
    } else if (jsonNode.isTextual()) {
      return jsonNode.textValue();
    } else if (jsonNode.isBoolean()) {
      return jsonNode.booleanValue();
    } else if (jsonNode.isNumber()) {
      if (jsonNode.isDouble() || jsonNode.isFloatingPointNumber()) {
        return jsonNode.doubleValue();
      } else {
        return jsonNode.longValue();
      }
    } else {
      return null;
    }
  }

  /**
   * Expands service parameters using the provided SourceTemplate.
   *
   * @param serviceSource the SourceTemplate to expand
   * @return a list of expanded service parameters
   */
  public static List<Map<String, Object>> expandServiceParameters(SourceTemplate serviceSource) {
    return ListExpansion.expandConfigTemplate(serviceSource.getPatternConfig());
  }

  /**
   * Expands connectors using the provided ConnectorTemplate.
   *
   * @param connectorTemplate the ConnectorTemplate to expand
   * @return a list of expanded connectors
   */
  private static List<Connector> expandConnector(ConnectorTemplate connectorTemplate) {
    return connectorTemplate.getTarget().getFault().getPercentage().stream()
            .flatMap(faultPercentage -> expandServiceParameters(connectorTemplate.getSource()).stream()
                    .map(sourcePattern -> new Connector(
                            connectorTemplate.getName(),
                            new Source(connectorTemplate.getSource().getService(), sourcePattern),
                            new Target(connectorTemplate.getTarget().getService(),
                                    ScenarioFaultTemplate.create(
                                            faultPercentage,
                                            connectorTemplate.getTarget().getFault().getDelay(),
                                            connectorTemplate.getTarget().getFault().getAbort())))))
            .collect(Collectors.toList());
  }

  /**
   * Generates combinations of lists.
   *
   * @param listOfLists the list of lists to generate combinations from
   * @param <T> the type of elements in the lists
   * @return a list of combinations
   */
  public static <T> List<List<T>> generateCombinations(List<List<T>> listOfLists) {
    List<List<T>> result = new ArrayList<>();
    generateCombinationsRecursive(listOfLists, 0, new ArrayList<>(), result);
    return result;
  }

  /**
   * Recursively generates combinations of lists.
   *
   * @param listOfLists the list of lists to generate combinations from
   * @param depth the current depth of recursion
   * @param currentCombination the current combination being built
   * @param result the list to store the combinations
   * @param <T> the type of elements in the lists
   */
  private static <T> void generateCombinationsRecursive(List<List<T>> listOfLists, int depth, List<T> currentCombination, List<List<T>> result) {
    if (depth == listOfLists.size()) {
      result.add(new ArrayList<>(currentCombination));
      return;
    }
    for (T element : listOfLists.get(depth)) {
      currentCombination.add(element);
      generateCombinationsRecursive(listOfLists, depth + 1, currentCombination, result);
      currentCombination.remove(currentCombination.size() - 1);
    }
  }

  /**
   * Creates a list of scenarios based on the provided benchmark and workload.
   *
   * @param benchmark the benchmark to create scenarios for
   * @param workload the workload to use for creating scenarios
   * @return a list of scenarios
   */
  public static List<Scenario> create(Benchmark benchmark, Workload workload) {
    return benchmark.getSpec().getScenarios().stream()
            .flatMap(scenarioTemplate -> createScenariosForTemplate(scenarioTemplate, workload, benchmark).stream())
            .collect(Collectors.toList());
  }

  /**
   * Creates scenarios for a given template, workload, and benchmark.
   *
   * @param scenarioTemplate the scenario template
   * @param workload the workload
   * @param benchmark the benchmark
   * @return a list of scenarios
   */
  private static List<Scenario> createScenariosForTemplate(ScenarioTemplate scenarioTemplate, Workload workload, Benchmark benchmark) {
    List<List<Connector>> expandedConnectors = scenarioTemplate.getConnectors().stream()
            .map(ScenarioFactory::expandConnector)
            .collect(Collectors.toList());

    List<List<Connector>> expandedConnectorsCombined = generateCombinations(expandedConnectors);
    List<Scenario> scenarios = new ArrayList<>();

    for (var workloadUser : workload.getSpec().getUsers()) {
      for (int i = 0; i < expandedConnectorsCombined.size(); i++) {
        String scenarioName = generateScenarioName(scenarioTemplate.getName() + "-" + workloadUser + "vu", i + 1);
        List<Connector> connectors = expandedConnectorsCombined.get(i);
        Scenario scenario = new Scenario();
        scenario.setSpec(new ScenarioSpec(
                scenarioName,
                new ScenarioWorkload(workload.getMetadata().getName(), workloadUser),
                connectors));
        scenario.setMetadata(createMeta(scenarioName, benchmark));
        scenarios.add(scenario);
      }
    }
    return scenarios;
  }

  /**
   * Generates a scenario name based on the template and index.
   *
   * @param scenarioName the base scenario name
   * @param index the index to append to the scenario name
   * @return the generated scenario name
   */
  private static String generateScenarioName(String scenarioName, int index) {
    return String.format("%s-%05d", scenarioName, index);
  }

  /**
   * Creates metadata for a scenario based on the benchmark.
   *
   * @param name the name of the scenario
   * @param benchmark the benchmark to create metadata from
   * @return the created metadata
   */
  private static ObjectMeta createMeta(String name, Benchmark benchmark) {
    return new ObjectMetaBuilder()
            .withName(name)
            .withNamespace(benchmark.getMetadata().getNamespace())
            .addToAnnotations(OWNED_BY, benchmark.getMetadata().getName())
            .build();
  }
}
