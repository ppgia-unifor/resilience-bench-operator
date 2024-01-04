package br.unifor.ppgia.resiliencebench.resources.benchmark;

import br.unifor.ppgia.resiliencebench.resources.PatternConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Source {

  private String service;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<PatternConfig> patternConfigs = new ArrayList<>();

  public Source() { }

  public Source(String service, List<PatternConfig> patternConfig) {
    this.service = service;
    this.patternConfigs = patternConfig;
  }

  public String getService() {
    return service;
  }


  public List<PatternConfig> getPatternConfigs() {
    return patternConfigs;
  }

  public void setPatternConfigs(List<PatternConfig> patternConfigs) {
    this.patternConfigs = patternConfigs;
  }
}
