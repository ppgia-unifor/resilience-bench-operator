package io.resiliencebench.models.scenario;

import java.util.Objects;

/**
 * Represents a target in the resilience benchmark.
 */
public class Target {

  private ScenarioFaultTemplate fault;
  private String serviceName;

  /**
   * Default constructor for Target.
   */
  public Target() {
    // Default constructor for serialization/deserialization
  }

  /**
   * Constructs a Target with the specified service name and fault template.
   *
   * @param serviceName the name of the service
   * @param fault       the fault template
   */
  public Target(String serviceName, ScenarioFaultTemplate fault) {
    this.serviceName = serviceName;
    this.fault = fault;
  }

  /**
   * Returns the fault template associated with the target.
   *
   * @return the fault template
   */
  public ScenarioFaultTemplate getFault() {
    return fault;
  }

  /**
   * Sets the fault template for the target.
   *
   * @param fault the fault template to set
   * @throws IllegalArgumentException if the fault is null
   */
  public void setFault(ScenarioFaultTemplate fault) {
    if (fault == null) {
      throw new IllegalArgumentException("Fault cannot be null");
    }
    this.fault = fault;
  }

  /**
   * Returns the name of the service.
   *
   * @return the service name
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * Sets the name of the service.
   *
   * @param serviceName the service name to set
   * @throws IllegalArgumentException if the service name is null or empty
   */
  public void setServiceName(String serviceName) {
    if (serviceName == null || serviceName.trim().isEmpty()) {
      throw new IllegalArgumentException("Service name cannot be null or empty");
    }
    this.serviceName = serviceName;
  }

  /**
   * Returns a string representation of the Target.
   *
   * @return a string representation of the Target
   */
  @Override
  public String toString() {
    return "Target{" +
            "fault=" + fault +
            ", serviceName='" + serviceName + '\'' +
            '}';
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare
   * @return true if this object is the same as the obj argument; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Target target = (Target) o;
    return Objects.equals(fault, target.fault) &&
            Objects.equals(serviceName, target.serviceName);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(fault, serviceName);
  }
}
