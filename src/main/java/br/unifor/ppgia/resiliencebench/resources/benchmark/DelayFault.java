package br.unifor.ppgia.resiliencebench.resources.benchmark;

public class DelayFault implements FaultConfiguration {

  private int duration;

  public int getDuration() {
    return duration;
  }
}
