package io.resiliencebench.models.fault;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Represents a delay fault in a system, encapsulating the delay duration in milliseconds.
 */
public record DelayFault(@JsonPropertyDescription("Delay duration in milliseconds") int duration) {

  /**
   * Constructs a DelayFault with the specified delay duration.
   *
   * @param duration the delay duration in milliseconds
   * @throws IllegalArgumentException if the duration is negative
   */
  public DelayFault {
    if (duration < 0) {
      throw new IllegalArgumentException("Delay duration must be non-negative.");
    }
  }

  /**
   * Returns a string representation of the delay fault.
   *
   * @return a string representation of the delay fault
   */
  @Override
  public String toString() {
    return String.format("delay-%dms", duration);
  }
}
