package br.unifor.ppgia.resiliencebench.resources.scenario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScenarioSpec {

  @JsonIgnore
  private static final ObjectMapper mapper = new ObjectMapper();

  private String targetServiceName;
  private String sourceServiceName;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();
  @JsonIgnore
  private Map<String, Object> internalPatternConfig = new LinkedHashMap<>();

  private ScenarioWorkload workload;
  private ScenarioFaultTemplate fault;

  public ScenarioSpec(
          String targetServiceName,
          String sourceServiceName,
          Map<String, Object> patternConfig,
          ScenarioWorkload workload,
          ScenarioFaultTemplate fault
  ) {
    this.targetServiceName = targetServiceName;
    this.sourceServiceName = sourceServiceName;
    this.workload = workload;
    this.fault = fault;
    patternConfig.forEach(this::addToPatternConfig);
  }

  public ScenarioSpec() {
  }

  public String getTargetServiceName() {
    return targetServiceName;
  }

  public String getSourceServiceName() {
    return sourceServiceName;
  }

  public ScenarioWorkload getWorkload() {
    return workload;
  }

  public ScenarioFaultTemplate getFault() {
    return fault;
  }

  public void addToPatternConfig(String name, Object value) {
    internalPatternConfig.put(name, mapper.valueToTree(value));
    patternConfig.put("patternConfig", mapper.valueToTree(internalPatternConfig));
  }

  /**
   * Returns a copy of the given expanded patternConfig
   */
  public Map<String, Object> getPatternConfig() {
    return toObjectMap(patternConfig.get("patternConfig"));
  }

  private static Map<String, Object> toObjectMap(JsonNode jsonNode) {
    Map<String, Object> resultMap = new LinkedHashMap<>();
    if (jsonNode != null && jsonNode.isObject()) {
      jsonNode.fields().forEachRemaining(entry -> resultMap.put(entry.getKey(), toObject(entry.getValue())));
    }
    return resultMap;
  }

  private static Object toObject(JsonNode jsonNode) {
    if (jsonNode.isObject()) {
      return toObjectMap(jsonNode);
    } else if (jsonNode.isArray()) {
      List<Object> list = new ArrayList<>();
      jsonNode.elements().forEachRemaining(element -> list.add(toObject(element)));
      return list;
    } else if (jsonNode.isTextual()) {
      return jsonNode.textValue();
    } else if (jsonNode.isBoolean()) {
      return jsonNode.booleanValue();
    } else if (jsonNode.isNumber()) {
      if (jsonNode.isDouble() || jsonNode.isFloatingPointNumber()) {
        return jsonNode.doubleValue();
      } else if (jsonNode.isInt()) {
        return jsonNode.intValue();
      } else {
        return jsonNode.longValue();
      }
    } else if (jsonNode.isNull()) {
      return null;
    }

    return jsonNode;
  }
}
