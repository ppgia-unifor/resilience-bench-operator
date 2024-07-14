package io.resiliencebench.resources;

import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.resiliencebench.resources.benchmark.Benchmark;
import io.resiliencebench.resources.benchmark.ConnectorTemplate;
import io.resiliencebench.resources.benchmark.ServiceTemplate;
import io.resiliencebench.resources.scenario.*;
import io.resiliencebench.resources.workload.Workload;

import java.util.*;

import static io.resiliencebench.resources.ListExpansion.*;
import static io.resiliencebench.support.Annotations.OWNED_BY;
import static java.util.Collections.emptyList;
import static java.util.List.*;

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

  public static List<Service> expandService(ServiceTemplate serviceTemplate) {
    if (serviceTemplate.getEnvs() == null) {
      return of(new Service(serviceTemplate.getName()));
    }
    var expandedEnvs = expandConfigTemplate(serviceTemplate.getEnvs());
    return expandedEnvs.stream().map(env -> new Service(serviceTemplate.getName(), env)).toList();
  }

  public static List<IstioPattern> expandIstioPattern(ConnectorTemplate connectorTemplate) {
    var pattern = connectorTemplate.getPattern();
    if (pattern == null) {
      return emptyList();
    }
    if (pattern.getIstio() == null) {
      return emptyList();
    }
    List<Map<String, Object>> retry = of(Map.of());
    List<Map<String, Object>> timeout = of(Map.of());
    List<Map<String, Object>> circuitBreaker = of(Map.of());

    if (pattern.getIstio().getRetry() != null) {
      retry = expandConfigTemplate(pattern.getIstio().getRetry());
    }
    if (pattern.getIstio().getTimeout() != null) {
      timeout = expandConfigTemplate(pattern.getIstio().getTimeout());
    }

    if (pattern.getIstio().getCircuitBreaker() != null) {
      circuitBreaker = expandConfigTemplate(pattern.getIstio().getCircuitBreaker());
    }

    var result = new ArrayList<IstioPattern>();
    for (var retryItem : retry) {
      for (var timeoutItem : timeout) {
        for (var circuitBreakerItem : circuitBreaker) {
          result.add(new IstioPattern(retryItem, timeoutItem, circuitBreakerItem));
        }
      }
    }
    return result;
  }

  private static List<Connector> expandConnector(ConnectorTemplate connectorTemplate) {
    List<Connector> expandedConnectors = new ArrayList<>();

    var sources = expandService(connectorTemplate.getSource());
    var destinations = expandService(connectorTemplate.getDestination());

    var istioPatterns = expandIstioPattern(connectorTemplate);
    List<Integer> faults = connectorTemplate.getFault() != null ? connectorTemplate.getFault().getPercentage() : of();

    var faultCount = faults.isEmpty() ? 1 : faults.size();
    var istioPatternsCount = istioPatterns.isEmpty() ? 1 : istioPatterns.size();

    for (int i = 0; i < faultCount; i++) {
      for (int k = 0; k < istioPatternsCount; k++) {
        for (var source : sources) {
          for (var destination : destinations) {
            var builder = new Connector.Builder()
                    .name(connectorTemplate.getName())
                    .source(source)
                    .destination(destination)
                    .fault(faults.isEmpty() ? null :
                            Fault.create(
                                    faults.get(i),
                                    connectorTemplate.getFault().getDelay(),
                                    connectorTemplate.getFault().getAbort()
                            ))
                    .istio(istioPatterns.isEmpty() ? null : istioPatterns.get(k));
            expandedConnectors.add(builder.build());
          }
        }
      }
    }

    return expandedConnectors;
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
          var scenarioName = generateScenarioName(scenarioTemplate.getName() + "-" + workloadUser + "vu", i+1);
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
    return scenarioName + "-" + "00000".substring(("" + index).length()) + index;
  }

  private static ObjectMeta createMeta(String name, Benchmark benchmark) {
    return new ObjectMetaBuilder()
            .withName(name)
            .withNamespace(benchmark.getMetadata().getNamespace())
            .addToAnnotations(OWNED_BY, benchmark.getMetadata().getName())
            .build();
  }
}
