package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

public class ScenarioFaultTemplate {

  private String provider;

  @JsonInclude(NON_EMPTY)
  private List<Integer> percentages = new ArrayList<>();
  @JsonInclude(NON_EMPTY)
  private List<String> services = new ArrayList<>();

  public ScenarioFaultTemplate(String provider, List<Integer> percentages, List<String> services) {
    this.provider = provider;
    this.percentages = percentages;
    this.services = services;
  }

  public ScenarioFaultTemplate() {
  }

  public String getProvider() {
    return provider;
  }

  public List<Integer> getPercentages() {
    return percentages;
  }

  public List<String> getServices() {
    return services;
  }
}
