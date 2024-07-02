package io.resiliencebench.models.benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the specification of a benchmark, including the workload and scenario templates.
 */
public class BenchmarkSpec {

  private String workload;
  private List<ScenarioTemplate> scenarios;

  /**
   * Default constructor for BenchmarkSpec.
   * Initializes an empty list of scenario templates.
   */
  public BenchmarkSpec() {
    this.scenarios = new ArrayList<>();
  }

  /**
   * Constructs a BenchmarkSpec with the specified workload and list of scenario templates.
   *
   * @param workload          the workload associated with the benchmark
   * @param scenarioTemplates the list of scenario templates
   */
  public BenchmarkSpec(String workload, List<ScenarioTemplate> scenarioTemplates) {
    this.workload = workload;
    this.scenarios = new ArrayList<>(scenarioTemplates);
  }

  /**
   * Returns the workload associated with the benchmark.
   *
   * @return the workload
   */
  public String getWorkload() {
    return workload;
  }

  /**
   * Sets the workload associated with the benchmark.
   *
   * @param workload the workload to set
   */
  public void setWorkload(String workload) {
    this.workload = workload;
  }

  /**
   * Returns the list of scenario templates.
   *
   * @return the list of scenario templates
   */
  public List<ScenarioTemplate> getScenarios() {
    return new ArrayList<>(scenarios); // Return a copy to preserve encapsulation
  }

  /**
   * Sets the list of scenario templates.
   *
   * @param scenarios the list of scenario templates to set
   */
  public void setScenarios(List<ScenarioTemplate> scenarios) {
    this.scenarios = new ArrayList<>(scenarios); // Store a copy to preserve encapsulation
  }

  @Override
  public String toString() {
    return "BenchmarkSpec{" +
            "workload='" + workload + '\'' +
            ", scenarios=" + scenarios +
            '}';
  }
}
