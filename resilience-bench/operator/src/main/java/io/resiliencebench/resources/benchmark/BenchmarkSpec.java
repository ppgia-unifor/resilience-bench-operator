package io.resiliencebench.resources.benchmark;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkSpec {

  private String workload;
  private List<ScenarioTemplate> scenarios = new ArrayList<>();

  public BenchmarkSpec() {
  }

  public BenchmarkSpec(String workload, List<ScenarioTemplate> scenarioTemplates) {
    this();
    this.workload = workload;
    this.scenarios = scenarioTemplates;
  }

  public String getWorkload() {
    return workload;
  }

  public List<ScenarioTemplate> getScenarios() {
    return scenarios;
  }
}
