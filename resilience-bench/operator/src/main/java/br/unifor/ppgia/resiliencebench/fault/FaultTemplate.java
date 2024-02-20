package br.unifor.ppgia.resiliencebench.fault;

import com.fasterxml.jackson.annotation.JsonInclude;

public abstract class FaultTemplate<P> {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private P percentage;
  private DelayFault delay;
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
