package io.resiliencebench.resources;

import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.resiliencebench.resources.benchmark.Benchmark;
import io.resiliencebench.resources.benchmark.ConnectorTemplate;
import io.resiliencebench.resources.benchmark.SourceTemplate;
import io.resiliencebench.resources.scenario.*;
import io.resiliencebench.resources.workload.Workload;

import java.util.*;

import static io.resiliencebench.support.Annotations.OWNED_BY;

public final class ScenarioFactory {

  public ScenarioFactory() {
    throw new IllegalStateException("Utility class");
  }

  private static Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
    Map<String, Object> resultMap = new HashMap<>();

    if (jsonNode != null && jsonNode.isObject()) {
      jsonNode.fields().forEachRemaining(entry -> resultMap.put(entry.getKey(), convertJsonNode(entry.getValue())));
    }
    return resultMap;
  }

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
    } else if (jsonNode.isNull()) {
      return null;
    }

    return jsonNode;
  }

  public static List<Map<String, Object>> expandServiceParameters(SourceTemplate serviceSource) {
    return ListExpansion.expandConfigTemplate(serviceSource.getPatternConfig());
  }

  private static List<Connector> expandConnector(ConnectorTemplate connectorTemplate) {
    List<Connector> expandedConnectors = new ArrayList<>();
    for (var faultPercentage : connectorTemplate.getTarget().getFault().getPercentage()) {
      var fault = ScenarioFaultTemplate.create(
              faultPercentage,
              connectorTemplate.getTarget().getFault().getDelay(),
              connectorTemplate.getTarget().getFault().getAbort()
      );
      for (var sourcePattern : expandServiceParameters(connectorTemplate.getSource())) {
        var source = new Source(connectorTemplate.getSource().getService(), sourcePattern);
        var target = new Target(connectorTemplate.getTarget().getService(), fault);
        expandedConnectors.add(new Connector(connectorTemplate.getName(), source, target));
      }
    }

    return expandedConnectors;
  }

  public static <T> List<List<T>> generateCombinations(List<List<T>> listOfLists) {
    List<List<T>> result = new ArrayList<>();
    generateCombinationsRecursive(listOfLists, 0, new ArrayList<>(), result);
    return result;
  }

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

  public static List<Scenario> create(Benchmark benchmark, Workload workload) {
    List<Scenario> executions = new ArrayList<>();

    for (var scenarioTemplate : benchmark.getSpec().getScenarios()) {
      var expandedConnectors = new ArrayList<List<Connector>>();
      for (var connectorTemplate : scenarioTemplate.getConnectors()) {
        expandedConnectors.add(expandConnector(connectorTemplate));
      }

      var expandedConnectorsCombined = new ArrayList<List<Connector>>();
      generateCombinationsRecursive(expandedConnectors, 0, new ArrayList<>(), expandedConnectorsCombined);

      var workloadUsers = workload.getSpec().getUsers();
      var workloadName = workload.getMetadata().getName();

      for (var workloadUser : workloadUsers) {
        for (int i = 0; i < expandedConnectorsCombined.size(); i++) {
          var scenarioName = generateScenarioName(scenarioTemplate.getName(), i+1);
          var connectors = expandedConnectorsCombined.get(i);
          var spec = new ScenarioSpec(
                  scenarioName,
                  new ScenarioWorkload(workloadName, workloadUser),
                  connectors);
          var scenario = new Scenario();
          scenario.setSpec(spec);
          scenario.setMetadata(createMeta(scenarioName, benchmark));
          executions.add(scenario);
        }
      }
    }

    return executions;
  }

  private static String generateScenarioName(String scenarioName, int index) {
    return scenarioName + "-" + "00000".substring((""+index).length()) + index;
  }

  private static ObjectMeta createMeta(String name, Benchmark benchmark) {
    return new ObjectMetaBuilder()
            .withName(name)
            .withNamespace(benchmark.getMetadata().getNamespace())
            .addToAnnotations(OWNED_BY, benchmark.getMetadata().getName())
            .build();
  }
}
