package br.unifor.ppgia.resiliencebench.resources.benchmark;

import br.unifor.ppgia.resiliencebench.resources.FaultType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class FaultTemplate {

  private int status;
  private FaultType type;
  private int duration;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> percentage = new ArrayList<>();

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public FaultType getType() {
    return type;
  }

  public void setType(FaultType type) {
    this.type = type;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public List<Integer> getPercentage() {
    return percentage;
  }

  public void setPercentage(List<Integer> percentage) {
    this.percentage = percentage;
  }
}
