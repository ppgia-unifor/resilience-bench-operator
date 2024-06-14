package io.resiliencebench.resources.benchmark;

public class TargetTemplate {

  private String service;
  private BenchmarkFaultTemplate fault;

  public TargetTemplate() {
  }

  public TargetTemplate(String service, BenchmarkFaultTemplate fault) {
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
