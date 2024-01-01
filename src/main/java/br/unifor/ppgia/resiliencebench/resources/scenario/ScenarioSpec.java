package br.unifor.ppgia.resiliencebench.resources.scenario;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

public class ScenarioSpec {

  @JsonIgnore
  private static final ObjectMapper mapper = new ObjectMapper();

  private String targetServiceName;
  private String sourceServiceName;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();

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
    this.patternConfig = patternConfig.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapper.valueToTree(entry.getValue())
            ));
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

  @JsonAnySetter
  public void setPatternConfig(String name, JsonNode value) {
    this.patternConfig.put(name, value);
  }

  @JsonAnyGetter
  public Map<String, JsonNode> getPatternConfig() {
    return patternConfig;
  }

  public Map<String, Object> patternConfigInObject() {
    return toObjectMap(getPatternConfig().get("patternConfig"));
  }

  private static Map<String, Object> toObjectMap(JsonNode jsonNode) {
    Map<String, Object> resultMap = new HashMap<>();

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
      } else {
        return jsonNode.longValue();
      }
    } else if (jsonNode.isNull()) {
      return null;
    }

    return jsonNode;
  }
}
