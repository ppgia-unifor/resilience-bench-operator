package br.unifor.ppgia.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class Fault {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> percentage = new ArrayList<>();

  private DelayFault delay;
  private AbortFault abort;

  public List<Integer> getPercentage() {
    return percentage;
  }

  public DelayFault getDelay() {
    return delay;
  }

  public AbortFault getAbort() {
    return abort;
  }

}
