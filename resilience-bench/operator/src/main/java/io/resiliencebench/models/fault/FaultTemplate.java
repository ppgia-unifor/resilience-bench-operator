package io.resiliencebench.models.fault;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Abstract class representing a fault template with a specified percentage.
 * The fault can be either a delay or an abort fault.
 *
 * @param <P> the type of the percentage parameter
 */
public abstract class FaultTemplate<P> {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private P percentage;
  private DelayFault delay;
  private AbortFault abort;

  /**
   * Default constructor.
   */
  public FaultTemplate() {
  }

  /**
   * Constructs a FaultTemplate with a delay fault.
   *
   * @param percentage the percentage of the fault
   * @param delay      the delay fault
   * @throws IllegalArgumentException if both delay and abort are provided or both are null
   */
  public FaultTemplate(P percentage, DelayFault delay) {
    validateParameters(percentage, delay, null);
    this.percentage = percentage;
    this.delay = delay;
  }

  /**
   * Constructs a FaultTemplate with an abort fault.
   *
   * @param percentage the percentage of the fault
   * @param abort      the abort fault
   * @throws IllegalArgumentException if both delay and abort are provided or both are null
   */
  public FaultTemplate(P percentage, AbortFault abort) {
    validateParameters(percentage, null, abort);
    this.percentage = percentage;
    this.abort = abort;
  }

  /**
   * Validates the parameters for the constructor.
   *
   * @param percentage the percentage of the fault
   * @param delay      the delay fault
   * @param abort      the abort fault
   * @throws IllegalArgumentException if both delay and abort are provided or both are null
   */
  private void validateParameters(P percentage, DelayFault delay, AbortFault abort) {
    if (percentage == null) {
      throw new IllegalArgumentException("Percentage must not be null.");
    }
    if ((delay == null && abort == null) || (delay != null && abort != null)) {
      throw new IllegalArgumentException("Exactly one of delay or abort must be provided.");
    }
  }

  /**
   * Returns the percentage of the fault.
   *
   * @return the percentage of the fault
   */
  public P getPercentage() {
    return percentage;
  }

  /**
   * Returns the delay fault, if any.
   *
   * @return the delay fault
   */
  public DelayFault getDelay() {
    return delay;
  }

  /**
   * Returns the abort fault, if any.
   *
   * @return the abort fault
   */
  public AbortFault getAbort() {
    return abort;
  }

  /**
   * Returns a string representation of the fault template.
   *
   * @return a string representation of the fault template
   */
  @Override
  public String toString() {
    return String.format("%s-%sp", (delay != null ? delay.toString() : abort.toString()), percentage);
  }
}
