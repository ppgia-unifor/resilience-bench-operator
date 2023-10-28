package br.unifor.ppgia.resiliencebench.resources.benchmark.scenario;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class Source {

  private String service;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig = new LinkedHashMap<>();

  public String getService() {
    return service;
  }

  public Map<String, JsonNode> getPatternConfig() {
    return patternConfig;
  }
}
