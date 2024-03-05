package io.resiliencebench.resources.benchmark;

public class Target {

  private String service;
  private BenchmarkFaultTemplate fault;

  public Target() {
  }

  public Target(String service, BenchmarkFaultTemplate fault) {
    this.service = service;
    this.fault = fault;
  }

  public BenchmarkFaultTemplate getFault() {
    return fault;
  }

  public String getService() {
    return service;
  }
}
