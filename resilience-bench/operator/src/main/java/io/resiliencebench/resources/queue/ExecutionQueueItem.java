package io.resiliencebench.resources.queue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ExecutionQueueItem {

  @JsonPropertyDescription("The name of the scenario it belongs to.")
  @JsonProperty(required = true)
  private String scenario;
  @JsonPropertyDescription("The status of the execution. Can be 'pending', 'running' or 'finished'. Automatically managed.")
  @JsonProperty(required = true)
  private String status;
  @JsonPropertyDescription("The time when the execution started.")
  private String startedAt;
  @JsonPropertyDescription("The time when the execution finished.")
  private String finishedAt;
  @JsonPropertyDescription("The path of the file with the item's results. Automatically created.")
  private String resultFile;

  public ExecutionQueueItem(String scenario, String resultFile) {
    this.scenario = scenario;
    this.resultFile = resultFile;
    this.status = Status.PENDING;
    this.finishedAt = "";
    this.startedAt = "";
  }

  public ExecutionQueueItem() {
  }

  public String getScenario() {
    return scenario;
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

  public String getResultFile() {
    return resultFile;
  }

  public interface Status {
    String PENDING = "pending";
    String RUNNING = "running";
    String FINISHED = "finished";
  }
}
