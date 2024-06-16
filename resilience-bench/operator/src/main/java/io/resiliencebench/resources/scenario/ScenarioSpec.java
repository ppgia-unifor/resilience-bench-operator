package io.resiliencebench.resources.scenario;

import io.vertx.core.json.JsonObject;

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

  @Override
  public String toString() {
    var json = new JsonObject();
    json.put("scenario", scenario);
    json.put("workload", workload);
    json.put("connectors", connectors);

    return json.toString();
  }
}
