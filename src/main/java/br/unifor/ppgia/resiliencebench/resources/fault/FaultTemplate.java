package br.unifor.ppgia.resiliencebench.resources.fault;

import com.fasterxml.jackson.annotation.JsonInclude;

public class FaultTemplate<P> {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private P percentage;
  private DelayFault delay;
  private AbortFault abort;

  public FaultTemplate() {
  }

  public FaultTemplate(P percentage, DelayFault delay, AbortFault abort) {
    this.percentage = percentage;
    this.delay = delay;
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
    
}
