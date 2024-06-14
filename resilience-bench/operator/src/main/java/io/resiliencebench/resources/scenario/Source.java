package io.resiliencebench.resources.scenario;

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
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();

  private String serviceName;

  public Source() {
  }

  public Source(
          String serviceName,
          Map<String, Object> patternConfig
  ) {
    this.serviceName = serviceName;
    this.patternConfig = patternConfig.entrySet().stream()
            .collect(LinkedHashMap::new,
                    (map, entry) -> map.put(entry.getKey(), mapper.valueToTree(entry.getValue())),
                    LinkedHashMap::putAll);
  }

  public String getServiceName() {
    return serviceName;
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
}
