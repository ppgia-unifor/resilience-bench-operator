package br.unifor.ppgia.resiliencebench.resources.benchmark;

public class Target {

  private String service;
  private BenchmarkFaultTemplate fault;

  public BenchmarkFaultTemplate getFault() {
    return fault;
  }

  public String getService() {
    return service;
  }
}
