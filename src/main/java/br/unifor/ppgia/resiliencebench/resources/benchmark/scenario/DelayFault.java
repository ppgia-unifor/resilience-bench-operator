package br.unifor.ppgia.resiliencebench.resources.benchmark.scenario;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class DelayFault {

  private int duration;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> percentage = new ArrayList<>();

  public int getDuration() {
    return duration;
  }

  public List<Integer> getPercentage() {
    return percentage;
  }
}
