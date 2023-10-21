package br.unifor.ppgia.resiliencebench.resources;

import br.unifor.ppgia.resiliencebench.resources.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioSpec;
import br.unifor.ppgia.resiliencebench.resources.scenario.Workload;
import br.unifor.ppgia.resiliencebench.resources.benchmark.Benchmark;
import br.unifor.ppgia.resiliencebench.resources.benchmark.FaultTemplate;
import br.unifor.ppgia.resiliencebench.resources.benchmark.WorkloadTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.unifor.ppgia.resiliencebench.resources.ListExpansion.*;

public final class ScenarioFactory {

  static List<Workload> expandWorkload(WorkloadTemplate spec) {
    return spec.getUsers().stream().map(users ->
            new Workload(users, spec.getDuration(), spec.getTargetUrl(), spec.getLocustFileConfigMap(), spec.getLocustUrl())
    ).toList();
  }

  static List<Fault> expandFault(FaultTemplate template) {
    return template.getPercentage().stream().map(percentage ->
            new Fault(template.getType(), percentage, template.getStatus(), template.getDuration())
    ).toList();
  }

  private static Map<String, Object> convertJsonNodeToMap(JsonNode jsonNode) {
    Map<String, Object> resultMap = new HashMap<>();

    if (jsonNode != null && jsonNode.isObject()) {
      jsonNode.fields().forEachRemaining(entry -> resultMap.put(entry.getKey(), convertJsonNode(entry.getValue())));
    }
    return resultMap;
  }

  private static Object convertJsonNode(JsonNode jsonNode) {
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

  static List<ResilientService> expandServices(ResilientService resilientService) {
    List<ResilientService> services = new ArrayList<>();
    var patternConfigTemplate = convertJsonNodeToMap(resilientService.getPatternConfig().get("patternConfig"));
    for (var patternConfig : expandConfigTemplate(patternConfigTemplate)) {
      services.add(
              new ResilientService(resilientService.getNamespace(), resilientService.getSelector(), "-pattern-config", patternConfig)
      );
    }
    return services;
  }

  public static List<Scenario> create(Benchmark resource) {
    var scenarios = new ArrayList<Scenario>();
//    for (var fault : expandFault(resource.getSpec().getFault())) {
//      for (var workload : expandWorkload(resource.getSpec().getWorkload())) {
//        for (var scenarioTemplate : resource.getSpec().getScenarios()) {
//          for (int round = 1; round <= resource.getSpec().getRounds(); round++) {
//            var spec = new ScenarioSpec(scenarioTemplate.getName(), round, workload, new Fault());
//            scenarios.add(new Scenario(spec));
//            for (var serviceTemplate : scenarioTemplate.getServices()) {
//              spec.addServices(expandServices(serviceTemplate));
//            }
//          }
//        }
//      }
//    }
    return scenarios;
  }
}
