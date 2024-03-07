package io.resiliencebench.resources.queue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Item {

  @JsonProperty(required = true)
  private String scenario;
  @JsonProperty(required = true)
  private String status;
  private String startedAt;
  private String finishedAt;

  private String resultFile;

  public Item(String scenario) {
    this.scenario = scenario;
    this.status = Status.PENDING;
    this.finishedAt = "";
    this.startedAt = "";
  }

  public Item() {
  }

  public String getScenario() {
    return scenario;
  }

  public String getStatus() {
    return status;
  }

  @JsonIgnore
  public boolean isFinished() {
    return status.equals(Status.FINISHED);
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setStartedAt(String startedAt) {
    this.startedAt = startedAt;
  }

  public void setFinishedAt(String finishedAt) {
    this.finishedAt = finishedAt;
  }

  @JsonIgnore
  public boolean isPending() {
    return status.equals(Status.PENDING);
  }

  @JsonIgnore
  public boolean isRunning() {
    return status.equals(Status.RUNNING);
  }

  public void setResultFile(String resultFile) {
    this.resultFile = resultFile;
  }

  public String getResultFile() {
    return resultFile;
  }

  interface Status {
    String PENDING = "pending";
    String RUNNING = "running";
    String FINISHED = "finished";
  }
}
