package io.resiliencebench.resources.queue;

import java.util.List;

public class ExecutionQueueSpec {

  private String resultFile;

  private List<ExecutionQueueItem> items;
  private String benchmark;

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
