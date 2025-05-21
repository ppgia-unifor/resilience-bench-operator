package io.resiliencebench.resources.fault;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public abstract class FaultTemplate<P> {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonPropertyDescription("The percentage of the fault to be applied")
  private P percentage;
  @JsonPropertyDescription("The delay fault. If applied the delay will be added to the response time")
  private DelayFault delay;
  @JsonPropertyDescription("The abort fault. If applied the request will be aborted with the specified status code")
  private AbortFault abort;

  public FaultTemplate() {
  }

  public FaultTemplate(P percentage, DelayFault delay) {
    this.percentage = percentage;
    this.delay = delay;
  }

  public FaultTemplate(P percentage, AbortFault abort) {
    this.percentage = percentage;
    this.abort = abort;
  }

  public P getPercentage() {
    return percentage;
  }

  public DelayFault getDelay() {
    return delay;
  }

  public AbortFault getAbort() {
    return abort;
  }

  @Override
  public String toString() {
    return (delay != null ? delay.toString() : abort.toString()) + "-" + percentage + "p";
  }
}
