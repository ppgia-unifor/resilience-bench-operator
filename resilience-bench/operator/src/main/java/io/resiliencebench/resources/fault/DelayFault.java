package io.resiliencebench.resources.fault;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record DelayFault(@JsonPropertyDescription("Delay duration in milliseconds") int duration) {
  @Override
  public String toString() {
    return "delay-" + duration + "ms";
  }
}
