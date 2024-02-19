package br.unifor.ppgia.resiliencebench.resources.execution.scenario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.time.ZoneId.of;

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

  private String executedAt;

  private String status;

  public ScenarioSpec(
          String targetServiceName,
          String sourceServiceName,
          Map<String, Object> patternConfig,
          ScenarioWorkload workload,
          ScenarioFaultTemplate fault
  ) {
    this.status = "pending";
    this.targetServiceName = targetServiceName;
    this.sourceServiceName = sourceServiceName;
    this.workload = workload;
    this.fault = fault;
    this.patternConfig = patternConfig.entrySet().stream()
            .collect(LinkedHashMap::new,
                    (map, entry) -> map.put(entry.getKey(), mapper.valueToTree(entry.getValue())),
                    LinkedHashMap::putAll);
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

  public void markAsExecuted() {
    this.status = "executed";
    this.executedAt = LocalDateTime.now(of("UTC")).toString();
  }

  public String getExecutedAt() {
    return executedAt;
  }

  public String getStatus() {
    return status;
  }

  /**
   * Returns a copy of the given expanded patternConfig
   */
  public Map<String, Object> getPatternConfig() {
    return patternConfig.entrySet().stream()
            .collect(LinkedHashMap::new,
                    (map, entry) -> map.put(entry.getKey(), toObject(entry.getValue())),
                    LinkedHashMap::putAll);
  }

  private static Object toObject(JsonNode jsonNode) {
    if (jsonNode.isArray()) {
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

  public void setStatus(String status) {
    this.status = status;
  }
}
