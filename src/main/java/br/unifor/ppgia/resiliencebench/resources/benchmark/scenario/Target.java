package br.unifor.ppgia.resiliencebench.resources.benchmark.scenario;

public class Target {

  private DelayFault delay;
  private AbortFault abort;

  public DelayFault getDelay() {
    return delay;
  }

  public AbortFault getAbort() {
    return abort;
  }

}
