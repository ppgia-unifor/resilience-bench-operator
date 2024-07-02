package io.resiliencebench.models.scenario;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the specification of a scenario including workload and connectors.
 */
public class ScenarioSpec {

  private ScenarioWorkload workload;
  private String scenario;
  private List<Connector> connectors = new ArrayList<>();

  /**
   * Default constructor.
   */
  public ScenarioSpec() {}

  /**
   * Constructs a new ScenarioSpec with the given parameters.
   *
   * @param scenario the name of the scenario
   * @param workload the workload associated with the scenario
   * @param connectors the list of connectors used in the scenario
   */
  public ScenarioSpec(String scenario, ScenarioWorkload workload, List<Connector> connectors) {
    this.workload = workload;
    this.scenario = scenario;
    this.connectors = connectors;
  }

  /**
   * Gets the workload associated with the scenario.
   *
   * @return the workload
   */
  public ScenarioWorkload getWorkload() {
    return workload;
  }

  /**
   * Gets the name of the scenario.
   *
   * @return the scenario name
   */
  public String getScenario() {
    return scenario;
  }

  /**
   * Gets the list of connectors used in the scenario.
   *
   * @return the list of connectors
   */
  public List<Connector> getConnectors() {
    return connectors;
  }

  /**
   * Converts the ScenarioSpec to a JSON object.
   *
   * @return the JSON representation of the scenario specification
   */
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put("scenario", scenario);
    json.put("workload", new JsonObject()
            .put("workloadName", workload.getWorkloadName())
            .put("users", workload.getUsers()));
    json.put("connectors", new JsonArray());

    for (Connector connector : connectors) {
      JsonObject fault = new JsonObject();
      if (connector.getTarget().getFault() != null) {
        var delay = connector.getTarget().getFault().getDelay();
        var abort = connector.getTarget().getFault().getAbort();
        fault.put("percentage", connector.getTarget().getFault().getPercentage())
                .put("delay", delay == null ? null : new JsonObject().put("duration", delay.duration()))
                .put("abort", abort == null ? null : new JsonObject().put("code", abort.httpStatus()));
      }

      JsonObject connectorJson = new JsonObject()
              .put("name", connector.getName())
              .put("source", new JsonObject()
                      .put("serviceName", connector.getSource().getServiceName())
                      .put("patternConfig", new JsonObject(connector.getSource().getPatternConfig())))
              .put("target", new JsonObject()
                      .put("serviceName", connector.getTarget().getServiceName())
                      .put("fault", fault));

      json.getJsonArray("connectors").add(connectorJson);
    }

    return json;
  }
}
