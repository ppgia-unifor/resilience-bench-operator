package br.unifor.ppgia.resiliencebench.resources.benchmark;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkSpec {
  private int rounds;
  private FaultTemplate fault;
  private WorkloadTemplate workload;
  private List<ScenarioTemplate> scenarios = new ArrayList<>();

  public int getRounds() {
    return rounds;
  }

  public void setRounds(int rounds) {
    this.rounds = rounds;
  }

  public FaultTemplate getFault() {
    return fault;
  }

  public void setFault(FaultTemplate fault) {
    this.fault = fault;
  }

  public WorkloadTemplate getWorkload() {
    return workload;
  }

  public void setWorkload(WorkloadTemplate workload) {
    this.workload = workload;
  }

  public List<ScenarioTemplate> getScenarios() {
    return scenarios;
  }

  public void setScenarios(List<ScenarioTemplate> scenarios) {
    this.scenarios = scenarios;
  }
}
