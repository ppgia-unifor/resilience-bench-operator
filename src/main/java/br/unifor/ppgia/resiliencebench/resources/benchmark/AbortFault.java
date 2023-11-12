package br.unifor.ppgia.resiliencebench.resources.benchmark;

public class AbortFault implements FaultConfiguration {

  private int httpStatus;

  public int getHttpStatus() {
    return httpStatus;
  }
}
