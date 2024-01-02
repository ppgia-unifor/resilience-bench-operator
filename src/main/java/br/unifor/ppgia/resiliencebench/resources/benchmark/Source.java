package br.unifor.ppgia.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class Source {

  private String service;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();

  public Source() { }

  public Source(String service, Map<String, JsonNode> patternConfig) {
    this.service = service;
    this.patternConfig = patternConfig;
  }

  public String getService() {
    return service;
  }

  @JsonAnyGetter
  public Map<String, JsonNode> getPatternConfig() {
    return patternConfig;
  }

  @JsonAnySetter
  public void setPatternConfig(String key, JsonNode value) {
    this.patternConfig.put(key, value);
  }
}
