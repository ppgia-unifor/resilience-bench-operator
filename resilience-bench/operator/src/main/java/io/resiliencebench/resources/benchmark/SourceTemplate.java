package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.resiliencebench.resources.NameValueProperties;

public class SourceTemplate {

  private String service;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private NameValueProperties patternConfig = new NameValueProperties();

  public SourceTemplate() { }

  public SourceTemplate(String service, NameValueProperties patternConfig) {
    this.service = service;
    this.patternConfig = patternConfig;
  }

  public String getService() {
    return service;
  }


  public NameValueProperties getPatternConfig() {
    return patternConfig;
  }

  public void setPatternConfig(NameValueProperties patternConfig) {
    this.patternConfig = patternConfig;
  }
}
