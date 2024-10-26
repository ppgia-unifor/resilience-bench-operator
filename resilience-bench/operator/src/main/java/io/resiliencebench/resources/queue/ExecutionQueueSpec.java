package io.resiliencebench.resources.queue;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public class ExecutionQueueSpec {

  @JsonPropertyDescription("The path of the file with the result's compilation. Automatically created.")
  private String resultFile;

  @JsonPropertyDescription("The name of the benchmark it belongs to.")
  private String benchmark;

  @JsonPropertyDescription("The list of items to execute.")
  private List<ExecutionQueueItem> items;

  public ExecutionQueueSpec() {
  }

  public ExecutionQueueSpec(String resultFile, List<ExecutionQueueItem> items, String benchmark) {
    this.resultFile = resultFile;
    this.items = items;
    this.benchmark = benchmark;
  }

  public List<ExecutionQueueItem> getItems() {
    return items;
  }

  public String getResultFile() {
    return resultFile;
  }

  public String getBenchmark() {
    return benchmark;
  }
}
