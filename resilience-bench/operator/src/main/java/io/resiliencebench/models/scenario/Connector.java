package io.resiliencebench.models.scenario;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Represents a connector with a source and target in a scenario.
 */
public class Connector {

  private String name;
  private Source source;
  private Target target;

  /**
   * Default constructor required for serialization/deserialization.
   */
  public Connector() {
  }

  /**
   * Constructs a new Connector with the specified name, source, and target.
   *
   * @param name   the name of the connector
   * @param source the source of the connector
   * @param target the target of the connector
   */
  @JsonCreator
  public Connector(@JsonProperty("name") String name,
          @JsonProperty("source") Source source,
          @JsonProperty("target") Target target) {
    this.name = name;
    this.source = source;
    this.target = target;
    validate();
  }

  /**
   * Returns the name of the connector.
   *
   * @return the name of the connector
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the connector.
   *
   * @param name the name of the connector
   */
  public void setName(String name) {
    this.name = name;
    validate();
  }

  /**
   * Returns the source of the connector.
   *
   * @return the source of the connector
   */
  public Source getSource() {
    return source;
  }

  /**
   * Sets the source of the connector.
   *
   * @param source the source of the connector
   */
  public void setSource(Source source) {
    this.source = source;
    validate();
  }

  /**
   * Returns the target of the connector.
   *
   * @return the target of the connector
   */
  public Target getTarget() {
    return target;
  }

  /**
   * Sets the target of the connector.
   *
   * @param target the target of the connector
   */
  public void setTarget(Target target) {
    this.target = target;
    validate();
  }

  /**
   * Validates the Connector instance.
   *
   * @throws IllegalArgumentException if any required field is null or invalid
   */
  private void validate() {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (source == null) {
      throw new IllegalArgumentException("Source cannot be null");
    }
    if (target == null) {
      throw new IllegalArgumentException("Target cannot be null");
    }
  }

  @Override
  public String toString() {
    return "Connector{" +
            "name='" + name + '\'' +
            ", source=" + source +
            ", target=" + target +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Connector connector = (Connector) o;
    return Objects.equals(name, connector.name) &&
            Objects.equals(source, connector.source) &&
            Objects.equals(target, connector.target);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, source, target);
  }

  /**
   * Builder class for constructing Connector instances.
   */
  public static class Builder {
    private String name;
    private Source source;
    private Target target;

    /**
     * Sets the name for the connector being built.
     *
     * @param name the name of the connector
     * @return the builder instance
     */
    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets the source for the connector being built.
     *
     * @param source the source of the connector
     * @return the builder instance
     */
    public Builder withSource(Source source) {
      this.source = source;
      return this;
    }

    /**
     * Sets the target for the connector being built.
     *
     * @param target the target of the connector
     * @return the builder instance
     */
    public Builder withTarget(Target target) {
      this.target = target;
      return this;
    }

    /**
     * Builds the Connector instance.
     *
     * @return the constructed Connector
     * @throws IllegalArgumentException if any required field is null or invalid
     */
    public Connector build() {
      Connector connector = new Connector(name, source, target);
      connector.validate();
      return connector;
    }
  }
}
