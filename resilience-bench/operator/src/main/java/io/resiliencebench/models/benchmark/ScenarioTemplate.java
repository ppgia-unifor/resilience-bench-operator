package io.resiliencebench.models.benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a scenario template, including the name and a list of connector templates.
 */
public class ScenarioTemplate {

  private String name;
  private List<ConnectorTemplate> connectors;

  /**
   * Default constructor for ScenarioTemplate.
   * Initializes an empty list of connector templates.
   */
  public ScenarioTemplate() {
    this.connectors = new ArrayList<>();
  }

  /**
   * Constructs a ScenarioTemplate with the specified name and list of connector templates.
   *
   * @param name       the name of the scenario template
   * @param connectors the list of connector templates
   */
  public ScenarioTemplate(String name, List<ConnectorTemplate> connectors) {
    this.name = name;
    this.connectors = new ArrayList<>(connectors);
  }

  /**
   * Returns the list of connector templates.
   *
   * @return the list of connector templates
   */
  public List<ConnectorTemplate> getConnectors() {
    return new ArrayList<>(connectors); // Return a copy to preserve encapsulation
  }

  /**
   * Sets the list of connector templates.
   *
   * @param connectors the list of connector templates to set
   */
  public void setConnectors(List<ConnectorTemplate> connectors) {
    this.connectors = new ArrayList<>(connectors); // Store a copy to preserve encapsulation
  }

  /**
   * Returns the name of the scenario template.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the scenario template.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "ScenarioTemplate{" +
            "name='" + name + '\'' +
            ", connectors=" + connectors +
            '}';
  }
}
