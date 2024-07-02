package io.resiliencebench.models.benchmark;

import io.fabric8.crd.generator.annotation.PrinterColumn;

/**
 * Represents the status of a benchmark, including the total number of scenarios.
 */
public class BenchmarkStatus {

  @PrinterColumn(name = "Total Scenarios", priority = 1)
  private int totalScenarios;

  /**
   * Default constructor for BenchmarkStatus.
   * Initializes the totalScenarios to zero.
   */
  public BenchmarkStatus() {
    this.totalScenarios = 0;
  }

  /**
   * Constructs a BenchmarkStatus with the specified total number of scenarios.
   *
   * @param totalScenarios the total number of scenarios
   */
  public BenchmarkStatus(int totalScenarios) {
    this.totalScenarios = totalScenarios;
  }

  /**
   * Returns the total number of scenarios.
   *
   * @return the total number of scenarios
   */
  public int getTotalScenarios() {
    return totalScenarios;
  }

  /**
   * Sets the total number of scenarios.
   *
   * @param totalScenarios the total number of scenarios to set
   */
  public void setTotalScenarios(int totalScenarios) {
    this.totalScenarios = totalScenarios;
  }

  @Override
  public String toString() {
    return "BenchmarkStatus{" +
            "totalScenarios=" + totalScenarios +
            '}';
  }
}
