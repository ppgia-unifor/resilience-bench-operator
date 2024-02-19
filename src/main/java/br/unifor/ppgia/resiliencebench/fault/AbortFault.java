package br.unifor.ppgia.resiliencebench.fault;

import io.fabric8.generator.annotation.Max;
import io.fabric8.generator.annotation.Min;

public record AbortFault(@Min(100) @Max(599) int httpStatus) {
  @Override
  public String toString() {
    return "abort-" + httpStatus + "";
  }
}