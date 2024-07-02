package io.resiliencebench.models.fault;

import io.fabric8.generator.annotation.Max;
import io.fabric8.generator.annotation.Min;

/**
 * Represents an abort fault in a system, encapsulating an HTTP status code.
 * The HTTP status code is validated to be within the range 100-599.
 */
public record AbortFault(@Min(100) @Max(599) int httpStatus) {

  /**
   * Constructs an AbortFault with the specified HTTP status code.
   *
   * @param httpStatus the HTTP status code for the abort fault
   * @throws IllegalArgumentException if the HTTP status code is not within the range 100-599
   */
  public AbortFault {
    if (httpStatus < 100 || httpStatus > 599) {
      throw new IllegalArgumentException("HTTP status code must be between 100 and 599.");
    }
  }

  /**
   * Returns a string representation of the abort fault.
   *
   * @return a string representation of the abort fault
   */
  @Override
  public String toString() {
    return String.format("abort-%d", httpStatus);
  }
}
