package io.resiliencebench.resources.scenario;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.vertx.core.json.JsonObject.mapFrom;

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

  public JsonObject toConnectorsInJson() {
    var json = new JsonObject();
    for (var connector : connectors) {
      var connectorJson = new JsonObject()
              .put("name", connector.getName())
              .put("source", mapFrom(connector.getSource()))
              .put("destination", mapFrom(connector.getDestination()));

      if (connector.getFault() != null) {
        connectorJson.put("fault", connector.getFault().toJson());
      }
      if (connector.getIstio() != null) {
        connectorJson.put("istio", connector.getIstio().toJson());
      }
      json.getJsonArray("connectors").add(connectorJson);
    }
    return json;
  }
}
