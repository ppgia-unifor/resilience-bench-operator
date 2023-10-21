package br.unifor.ppgia.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.fabric8.kubernetes.api.model.LabelSelector;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResilientServiceTemplate {

  private String namespace;
  private String strategy;
  private LabelSelector selector;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();

  public ResilientServiceTemplate() {
  }

  public ResilientServiceTemplate(String namespace, LabelSelector selector, String config, Map<String, Object> patternConfig) {
    this.namespace = namespace;
    this.selector = selector;
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


  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getStrategy() {
    return strategy;
  }

  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }

  public LabelSelector getSelector() {
    return selector;
  }

  public void setSelector(LabelSelector selector) {
    this.selector = selector;
  }

  @JsonAnyGetter
  public Map<String, JsonNode> getPatternConfig() {
    return patternConfig;
  }

  @JsonAnySetter
  public void setPatternConfig(String name, JsonNode value) {
    this.patternConfig.put(name, value);
  }
}
