package io.resiliencebench.models.benchmark;

/**
 * Represents a connector template, including the name, source, and target.
 */
public class ConnectorTemplate {

  private String name;
  private SourceTemplate source;
  private TargetTemplate target;

  /**
   * Default constructor for ConnectorTemplate.
   */
  public ConnectorTemplate() {
    // Default constructor
  }

  /**
   * Constructs a ConnectorTemplate with the specified name, source, and target.
   *
   * @param name   the name of the connector template
   * @param source the source template
   * @param target the target template
   */
  public ConnectorTemplate(String name, SourceTemplate source, TargetTemplate target) {
    this.name = name;
    this.source = source;
    this.target = target;
  }

  /**
   * Returns the name of the connector template.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the connector template.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the source template of the connector.
   *
   * @return the source template
   */
  public SourceTemplate getSource() {
    return source;
  }

  /**
   * Sets the source template of the connector.
   *
   * @param source the source template to set
   */
  public void setSource(SourceTemplate source) {
    this.source = source;
  }

  /**
   * Returns the target template of the connector.
   *
   * @return the target template
   */
  public TargetTemplate getTarget() {
    return target;
  }

  /**
   * Sets the target template of the connector.
   *
   * @param target the target template to set
   */
  public void setTarget(TargetTemplate target) {
    this.target = target;
  }

  @Override
  public String toString() {
    return "ConnectorTemplate{" +
            "name='" + name + '\'' +
            ", source=" + source +
            ", target=" + target +
            '}';
  }
}
