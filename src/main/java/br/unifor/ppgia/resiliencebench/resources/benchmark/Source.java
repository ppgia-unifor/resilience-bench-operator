package br.unifor.ppgia.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Source {

  @JsonIgnore
  private static final ObjectMapper mapper = new ObjectMapper();

  private String service;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();
  @JsonIgnore
  private Map<String, Object> internalPatternConfig = new LinkedHashMap<>();


  public Source() { }

  public Source(String service, Map<String, JsonNode> patternConfig) {
    this.service = service;
    this.patternConfig = patternConfig;
  }

  public String getService() {
    return service;
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
