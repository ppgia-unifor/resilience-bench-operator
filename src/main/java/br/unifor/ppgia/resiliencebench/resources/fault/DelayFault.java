package br.unifor.ppgia.resiliencebench.resources.fault;

public record DelayFault(int duration) {
  @Override
  public String toString() {
    return "delay-" + duration + "ms";
  }
}
