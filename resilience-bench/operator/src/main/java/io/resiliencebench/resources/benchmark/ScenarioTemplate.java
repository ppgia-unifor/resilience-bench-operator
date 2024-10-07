package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.ArrayList;
import java.util.List;

public class ScenarioTemplate {

  @JsonPropertyDescription("The name of the scenario template")
  private String name;
  @JsonPropertyDescription("The set of connectors to be processed and then generated as scenarios")
  private List<ConnectorTemplate> connectors = new ArrayList<>();

  public ScenarioTemplate() {
  }

  public ScenarioTemplate(String name, List<ConnectorTemplate> connectors) {
    this.name = name;
    this.connectors = connectors;
  }

  public List<ConnectorTemplate> getConnectors() {
    return connectors;
  }

  public String getName() {
    return name;
  }
}
