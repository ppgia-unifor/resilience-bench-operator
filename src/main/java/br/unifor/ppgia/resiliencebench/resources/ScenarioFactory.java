package br.unifor.ppgia.resiliencebench.resources;

import br.unifor.ppgia.resiliencebench.resources.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.benchmark.Source;
import br.unifor.ppgia.resiliencebench.resources.benchmark.Target;
import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioFaultTemplate;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioSpec;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioWorkload;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import static br.unifor.ppgia.resiliencebench.resources.ListExpansion.expandConfigTemplate;
import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

public final class ScenarioFactory {
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

  static List<Map<String, Object>> expandServices(Source serviceSource) {
    var patternConfigTemplate = convertJsonNodeToMap(serviceSource.getPatternConfig().get("patternConfig"));
    return expandConfigTemplate(patternConfigTemplate);
  }

  public static List<Scenario> create(Benchmark benchmark, Workload workload) {
    List<Scenario> scenarios = new ArrayList<>();
    var workloadUsers = workload.getSpec().getUsers();
    var workloadName = workload.getMetadata().getName();

    for (var connection : benchmark.getSpec().getConnections()) {
      var target = connection.getTarget();
      var source = connection.getSource();

      for (var faultPercentage : target.getFault().getPercentage()) {
        var fault = new ScenarioFaultTemplate(faultPercentage, target.getFault().getDelay(),
                target.getFault().getAbort());

        for (var sourcePatternsParameters : expandServices(source)) {
          for (var workloadUser : workloadUsers) {
            scenarios.add(createScenario(workloadName, target, source, fault, sourcePatternsParameters, workloadUser));
          }
        }
      }
    }
    return scenarios.stream().flatMap(obj -> nCopies(benchmark.getSpec().getRounds(), obj).stream()).collect(toList());
  }

  private static Scenario createScenario(String workloadName, Target target, Source source, ScenarioFaultTemplate fault, Map<String, Object> sourcePatternsParameters, Integer workloadUser) {
    return new Scenario(
            new ScenarioSpec(
                    target.getService(),
                    source.getService(),
                    sourcePatternsParameters,
                    new ScenarioWorkload(workloadName, workloadUser),
                    fault)
    );
  }
}
