package br.unifor.ppgia.resiliencebench.resources.fault;

public record AbortFault(int httpStatus) {
  @Override
  public String toString() {
    return "abort-" + httpStatus + "";
  }
}