package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.resiliencebench.resources.PatternConfig;

public class SourceTemplate {

  private String service;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private PatternConfig patternConfig = new PatternConfig();

  public SourceTemplate() { }

  public SourceTemplate(String service, PatternConfig patternConfig) {
    this.service = service;
    this.patternConfig = patternConfig;
  }

  public String getService() {
    return service;
  }


  public PatternConfig getPatternConfig() {
    return patternConfig;
  }

  public void setPatternConfig(PatternConfig patternConfig) {
    this.patternConfig = patternConfig;
  }
}
