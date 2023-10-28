package br.unifor.ppgia.resiliencebench.resources.benchmark.scenario;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class AbortFault {

  private int httpStatus;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> percentage = new ArrayList<>();

  public int getHttpStatus() {
    return httpStatus;
  }

  public List<Integer> getPercentage() {
    return percentage;
  }

}
