package io.resiliencebench.resources.queue;

public class ExecutionQueueStatus {


  private long running;
  private long pending;
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
