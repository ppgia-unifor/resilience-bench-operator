package br.unifor.ppgia.resiliencebench.resources.workload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class WorkloadSpec {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> users = new ArrayList<>();
  private int duration;
  private String targetUrl;
  private String locustFileConfigMap;
  private String locustUrl;

  public List<Integer> getUsers() {
    return users;
  }

  public void setUsers(List<Integer> users) {
    this.users = users;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getTargetUrl() {
    return targetUrl;
  }

  public void setTargetUrl(String targetUrl) {
    this.targetUrl = targetUrl;
  }

  public String getLocustFileConfigMap() {
    return locustFileConfigMap;
  }

  public void setLocustFileConfigMap(String locustFileConfigMap) {
    this.locustFileConfigMap = locustFileConfigMap;
  }

  public void setLocustUrl(String locustUrl) {
    this.locustUrl = locustUrl;
  }

  public String getLocustUrl() {
    return locustUrl;
  }
}
