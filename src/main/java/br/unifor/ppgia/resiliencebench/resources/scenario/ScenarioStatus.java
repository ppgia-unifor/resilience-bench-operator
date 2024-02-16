package br.unifor.ppgia.resiliencebench.resources.scenario;

import java.time.LocalDateTime;

public class ScenarioStatus {

  private LocalDateTime executedAt;
  private String status;

  ScenarioStatus() {
  }

  public ScenarioStatus(LocalDateTime executedAt, String status) {
    this.executedAt = executedAt;
    this.status = status;
  }

  public LocalDateTime getExecutedAt() {
    return executedAt;
  }

  public String getStatus() {
    return status;
  }
}
