package br.unifor.ppgia.resiliencebench.resources.benchmark;

import br.unifor.ppgia.resiliencebench.resources.PatternConfig;
import com.fasterxml.jackson.annotation.JsonInclude;

public class Source {

  private String service;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private PatternConfig patternConfig = new PatternConfig();

  public Source() { }

  public Source(String service, PatternConfig patternConfig) {
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
