package io.resiliencebench.models.scenario;

import io.resiliencebench.models.fault.AbortFault;
import io.resiliencebench.models.fault.DelayFault;
import io.resiliencebench.models.fault.FaultTemplate;
import java.util.Objects;

/**
 * Represents a fault template specifically for scenarios.
 * This template supports both delay and abort faults.
 */
public class ScenarioFaultTemplate extends FaultTemplate<Integer> {

  private final Object fault; // Store the fault as a generic Object

  /**
   * Default constructor for ScenarioFaultTemplate.
   */
  public ScenarioFaultTemplate() {
    super();
    this.fault = null;
  }

  /**
   * Constructs a ScenarioFaultTemplate with the specified percentage and abort fault.
   *
   * @param percentage the percentage for the fault template
   * @param abort the abort fault
   */
  public ScenarioFaultTemplate(Integer percentage, AbortFault abort) {
    super(percentage, abort);
    this.fault = abort;
  }

  /**
   * Constructs a ScenarioFaultTemplate with the specified percentage and delay fault.
   *
   * @param percentage the percentage for the fault template
   * @param delay the delay fault
   */
  public ScenarioFaultTemplate(Integer percentage, DelayFault delay) {
    super(percentage, delay);
    this.fault = delay;
  }

  /**
   * Creates a new ScenarioFaultTemplate with the specified percentage, delay fault, and abort fault.
   * If both faults are provided, the delay fault takes precedence.
   *
   * @param percentage the percentage for the fault template
   * @param delay the delay fault, may be null
   * @param abort the abort fault, may be null
   * @return a new ScenarioFaultTemplate instance or null if both faults are null
   */
  public static ScenarioFaultTemplate create(Integer percentage, DelayFault delay, AbortFault abort) {
    if (delay != null) {
      return new ScenarioFaultTemplate(percentage, delay);
    } else if (abort != null) {
      return new ScenarioFaultTemplate(percentage, abort);
    } else {
      return null;
    }
  }

  /**
   * Returns the fault associated with this template.
   *
   * @return the fault
   */
  public Object getFault() {
    return fault;
  }

  /**
   * Validates the provided parameters.
   *
   * @param percentage the percentage to validate
   * @param fault the fault to validate
   * @throws IllegalArgumentException if any parameter is invalid
   */
  private void validateParameters(Integer percentage, Object fault) {
    Objects.requireNonNull(percentage, "Percentage cannot be null");
    Objects.requireNonNull(fault, "Fault cannot be null");
    if (percentage < 0 || percentage > 100) {
      throw new IllegalArgumentException("Percentage must be between 0 and 100");
    }
  }

  /**
   * Returns a string representation of the ScenarioFaultTemplate.
   *
   * @return a string representation of the ScenarioFaultTemplate
   */
  @Override
  public String toString() {
    return "ScenarioFaultTemplate{" +
            "percentage=" + getPercentage() +
            ", fault=" + fault +
            '}';
  }

  /**
   * Compares this ScenarioFaultTemplate to another object.
   *
   * @param o the object to compare to
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScenarioFaultTemplate that = (ScenarioFaultTemplate) o;
    return Objects.equals(getPercentage(), that.getPercentage()) &&
            Objects.equals(fault, that.fault);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(getPercentage(), fault);
  }
}
