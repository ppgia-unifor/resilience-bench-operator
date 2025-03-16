package io.resiliencebench.resources.queue;

import io.fabric8.crd.generator.annotation.PrinterColumn;

public class ExecutionQueueStatus {


  @PrinterColumn(name = "Pending")
  private long pending;
  @PrinterColumn(name = "Running")
  private long running;
  @PrinterColumn(name = "Finished")
  private long finished;

  public ExecutionQueueStatus() {
  }

  public ExecutionQueueStatus(long running, long pending, long finished) {
    this.running = running;
    this.pending = pending;
    this.finished = finished;
  }

  public long getRunning() {
    return running;
  }

  public long getPending() {
    return pending;
  }

  public long getFinished() {
    return finished;
  }
}
