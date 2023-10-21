package br.unifor.ppgia.resiliencebench.resources;

import br.unifor.ppgia.resiliencebench.resources.FaultType;

public class Fault {

  private FaultType type;
  private Integer percentage;
  private Integer status;
  private Integer duration;

  public Fault() {
  }

  public Fault(FaultType type, Integer percentage, Integer status, Integer duration) {
    this.type = type;
    this.percentage = percentage;
    this.status = status;
    this.duration = duration;
  }

  public FaultType getType() {
    return type;
  }

  public void setType(FaultType type) {
    this.type = type;
  }

  public Integer getPercentage() {
    return percentage;
  }

  public void setPercentage(Integer percentage) {
    this.percentage = percentage;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }
}
