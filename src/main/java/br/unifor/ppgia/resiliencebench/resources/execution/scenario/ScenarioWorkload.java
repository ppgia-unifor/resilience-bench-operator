package br.unifor.ppgia.resiliencebench.resources.execution.scenario;

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

  public void setWorkloadName(String workloadName) {
    this.workloadName = workloadName;
  }

  public int getUsers() {
    return users;
  }

  public void setUsers(int users) {
    this.users = users;
  }

  public String toString() {
    return String.format("%s-%d", workloadName, users);
  }
}
