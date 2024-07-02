package io.resiliencebench.resources.queue;

import java.util.List;

public class ExecutionQueueSpec {

  private String resultFile;

  private List<Item> items;
  private String benchmark;

  public ExecutionQueueSpec() {
  }

  public ExecutionQueueSpec(String resultFile, List<Item> items, String benchmark) {
    this.resultFile = resultFile;
    this.items = items;
    this.benchmark = benchmark;
  }

  public List<Item> getItems() {
    return items;
  }

  public String getResultFile() {
    return resultFile;
  }

  public String getBenchmark() {
    return benchmark;
  }
}
