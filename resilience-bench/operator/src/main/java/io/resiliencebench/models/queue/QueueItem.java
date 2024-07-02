package io.resiliencebench.models.queue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.resiliencebench.models.enums.QueueItemStatus;

/**
 * Represents an item in the execution queue.
 */
public class QueueItem {

  @JsonProperty(required = true)
  private String scenario;

  @JsonProperty(required = true)
  private QueueItemStatus status;

  private String startedAt;
  private String finishedAt;
  private String resultFile;

  /**
   * Constructs a new QueueItem with the specified scenario.
   *
   * @param scenario the scenario associated with this queue item
   */
  public QueueItem(String scenario) {
    this.scenario = scenario;
    this.status = QueueItemStatus.PENDING;
    this.finishedAt = "";
    this.startedAt = "";
  }

  /**
   * Default constructor required for deserialization.
   */
  public QueueItem() {
  }

  /**
   * Returns the scenario associated with this queue item.
   *
   * @return the scenario
   */
  public String getScenario() {
    return scenario;
  }

  /**
   * Returns whether this queue item is finished.
   *
   * @return true if the status is FINISHED, false otherwise
   */
  @JsonIgnore
  public boolean isFinished() {
    return status == QueueItemStatus.FINISHED;
  }

  /**
   * Sets the status of this queue item.
   *
   * @param status the new status
   */
  public void setStatus(QueueItemStatus status) {
    this.status = status;
  }

  /**
   * Sets the start time of this queue item.
   *
   * @param startedAt the start time
   */
  public void setStartedAt(String startedAt) {
    this.startedAt = startedAt;
  }

  /**
   * Sets the finish time of this queue item.
   *
   * @param finishedAt the finish time
   */
  public void setFinishedAt(String finishedAt) {
    this.finishedAt = finishedAt;
  }

  /**
   * Returns whether this queue item is pending.
   *
   * @return true if the status is PENDING, false otherwise
   */
  @JsonIgnore
  public boolean isPending() {
    return status == QueueItemStatus.PENDING;
  }

  /**
   * Returns whether this queue item is running.
   *
   * @return true if the status is RUNNING, false otherwise
   */
  @JsonIgnore
  public boolean isRunning() {
    return status == QueueItemStatus.RUNNING;
  }

  /**
   * Sets the result file for this queue item.
   *
   * @param resultFile the result file
   */
  public void setResultFile(String resultFile) {
    this.resultFile = resultFile;
  }

  /**
   * Returns the result file for this queue item.
   *
   * @return the result file
   */
  public String getResultFile() {
    return resultFile;
  }
}
