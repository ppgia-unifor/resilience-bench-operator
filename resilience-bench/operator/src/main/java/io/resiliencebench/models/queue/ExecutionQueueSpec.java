package io.resiliencebench.models.queue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the specification for an ExecutionQueue in the resilience bench system.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionQueueSpec {

  @JsonProperty("resultFile")
  private String resultFile;

  @JsonProperty("queueItems")
  private List<QueueItem> queueItems = new ArrayList<>();

  @JsonProperty("benchmark")
  private String benchmark;

  /**
   * Default constructor.
   */
  public ExecutionQueueSpec() {
    // Default constructor required for deserialization
  }

  /**
   * Constructs a new ExecutionQueueSpec with the specified parameters.
   *
   * @param resultFile the file path where the results will be stored
   * @param queueItems the list of queue items
   * @param benchmark  the benchmark associated with the queue
   */
  public ExecutionQueueSpec(String resultFile, List<QueueItem> queueItems, String benchmark) {
    this.resultFile = resultFile;
    this.queueItems = queueItems != null ? queueItems : new ArrayList<>();
    this.benchmark = benchmark;
  }

  /**
   * Returns the list of queue items.
   *
   * @return the list of queue items
   */
  public List<QueueItem> getQueueItems() {
    return queueItems;
  }

  /**
   * Sets the list of queue items.
   *
   * @param queueItems the list of queue items to set
   */
  public void setQueueItems(List<QueueItem> queueItems) {
    this.queueItems = queueItems != null ? queueItems : new ArrayList<>();
  }

  /**
   * Returns the result file path.
   *
   * @return the result file path
   */
  public String getResultFile() {
    return resultFile;
  }

  /**
   * Sets the result file path.
   *
   * @param resultFile the result file path to set
   */
  public void setResultFile(String resultFile) {
    this.resultFile = resultFile;
  }

  /**
   * Returns the benchmark associated with the queue.
   *
   * @return the benchmark associated with the queue
   */
  public String getBenchmark() {
    return benchmark;
  }

  /**
   * Sets the benchmark associated with the queue.
   *
   * @param benchmark the benchmark to set
   */
  public void setBenchmark(String benchmark) {
    this.benchmark = benchmark;
  }
}
