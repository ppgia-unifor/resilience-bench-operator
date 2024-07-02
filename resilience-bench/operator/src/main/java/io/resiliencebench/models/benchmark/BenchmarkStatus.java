package io.resiliencebench.resources.benchmark;

import io.fabric8.crd.generator.annotation.PrinterColumn;

public class BenchmarkStatus {

  @PrinterColumn(name = "Total Scenarios", priority = 1)
  private int totalScenarios;

  public BenchmarkStatus() {
  }

  public BenchmarkStatus(int totalScenarios) {
    this.totalScenarios = totalScenarios;
  }

  public int getTotalScenarios() {
    return totalScenarios;
  }
}
