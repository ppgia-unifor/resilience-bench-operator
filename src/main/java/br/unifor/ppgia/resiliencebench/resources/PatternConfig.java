package br.unifor.ppgia.resiliencebench.resources;

import com.fasterxml.jackson.databind.JsonNode;

public class PatternConfig {

  public PatternConfig() {
  }

  public PatternConfig(String name, JsonNode value) {
    this.name = name;
    this.value = value;
  }

  private String name;
  private JsonNode value;

  public String getName() {
    return name;
  }

  public JsonNode getValue() {
    return value;
  }
}
