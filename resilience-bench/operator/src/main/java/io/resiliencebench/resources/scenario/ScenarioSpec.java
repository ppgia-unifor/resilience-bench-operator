package io.resiliencebench.resources.scenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioSpec {

  private ScenarioWorkload workload;

  private String scenario;

  private List<Connector> connectors = new ArrayList<>();

  public ScenarioSpec() {
  }

  public ScenarioSpec(String scenario, ScenarioWorkload workload, List<Connector> connectors) {
    this.workload = workload;
    this.scenario = scenario;
    this.connectors = connectors;
  }

  public ScenarioWorkload getWorkload() {
    return workload;
  }

  public String getScenario() {
    return scenario;
  }

  public List<Connector> getConnectors() {
    return connectors;
  }
}
