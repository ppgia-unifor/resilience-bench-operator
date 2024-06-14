package io.resiliencebench.resources.benchmark;

import java.util.ArrayList;
import java.util.List;

public class ScenarioTemplate {

  private String name;
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
