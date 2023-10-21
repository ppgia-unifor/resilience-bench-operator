package br.unifor.ppgia.resiliencebench.resources;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.fabric8.kubernetes.api.model.LabelSelector;

import java.util.*;

public class ResilientService {

  private String name;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();

  public ResilientService() { }

  public ResilientService(String namespace, LabelSelector selector, String config, Map<String, Object> patternConfig) {
    for (var key : patternConfig.keySet()) {
      var value = patternConfig.get(key);
      if (value instanceof List<?>) {
        var listValue = (List<Double>) value;
        var arrayNode = JsonNodeFactory.instance.arrayNode();
        listValue.forEach(arrayNode::add);
        this.patternConfig.put(key, arrayNode);
      } else if (value instanceof Double) {
        this.patternConfig.put(key, JsonNodeFactory.instance.numberNode((Double) value));
      } else if (value instanceof Long) {
        this.patternConfig.put(key, JsonNodeFactory.instance.numberNode((Long) value));
      }
    }
  }

  @JsonAnyGetter
  public Map<String, JsonNode> getPatternConfig() {
    return patternConfig;
  }

  @JsonAnySetter
  public void setPatternConfig(String name, JsonNode value) {
    this.patternConfig.put(name, value);
  }

  public Map<String, Object> patternConfig() {
    return convertJsonNodeToMap(getPatternConfig().get("patternConfig"));
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

//  @Override
//  public List<ResilientService> expand() {
//    List<ResilientService> services = new ArrayList<>();
//    for (var patternConfig : ListExpansion.expandConfigTemplate(patternConfig())) {
//      var svc = new ResilientService(getNamespace(), getSelector(), "-pattern-config", patternConfig);
//      services.add(svc);
//    }
//    return services;
//  }
}
