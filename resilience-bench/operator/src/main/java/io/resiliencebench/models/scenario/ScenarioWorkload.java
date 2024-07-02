package io.resiliencebench.resources.scenario;

public class ScenarioWorkload {
  private String workloadName;
  private int users;

  public ScenarioWorkload() {
  }

  public ScenarioWorkload(String workloadName, int users) {
    this.workloadName = workloadName;
    this.users = users;
  }

  public String getWorkloadName() {
    return workloadName;
  }

  public int getUsers() {
    return users;
  }

  public String toString() {
    return String.format("%s-%d", workloadName, users);
  }
}
