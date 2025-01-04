package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.ArrayList;
import java.util.List;

public class ScenarioTemplate {

  @JsonPropertyDescription("The name of the scenario template")
  private String name;
  @JsonPropertyDescription("The set of connectors to be processed and then generated as scenarios")
  private List<ConnectorTemplate> connectors = new ArrayList<>();

  @JsonPropertyDescription("The fault to be applied to the services mentioned in the scenario")
  private ScenarioFaultTemplate fault;

  public ScenarioTemplate() {
  }

  public ScenarioTemplate(String name, List<ConnectorTemplate> connectors) {
    this.name = name;
    this.connectors = connectors;
  }

  public ScenarioTemplate(String name, List<ConnectorTemplate> connectors, ScenarioFaultTemplate fault) {
    this.name = name;
    this.connectors = connectors;
    this.fault = fault;
  }

  public List<ConnectorTemplate> getConnectors() {
    return connectors;
  }

  public String getName() {
    return name;
  }

  public ScenarioFaultTemplate getFault() {
    return fault;
  }
}
