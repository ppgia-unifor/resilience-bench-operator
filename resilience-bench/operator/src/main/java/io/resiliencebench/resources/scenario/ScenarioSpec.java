package io.resiliencebench.resources.scenario;

import io.vertx.core.json.JsonArray;
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


  public JsonObject toJson() {
    var json = new JsonObject();
    json.put("scenario", scenario);
    json.put(
            "workload",
            new JsonObject().put("workloadName", workload.getWorkloadName()).put("users", workload.getUsers())
    );
    json.put("connectors", new JsonArray());
    for (var connector : connectors) {
      json.getJsonArray("connectors").add(new JsonObject().put("name", connector.getName())
              .put("source", new JsonObject().put("serviceName", connector.getSource().getServiceName()))
              .put("target", new JsonObject().put("serviceName", connector.getTarget().getServiceName()))
      );
    }

    return json;
  }
}
