package br.unifor.ppgia.resiliencebench.resources.workload;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class WorkloadSpec {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> users = new ArrayList<>();
  private int duration;
  private String targetUrl;
  private ScriptConfig script;

  public WorkloadSpec() {
  }

  public WorkloadSpec(List<Integer> users, int duration, String targetUrl, ScriptConfig script) {
    this.users = users;
    this.duration = duration;
    this.targetUrl = targetUrl;
    this.script = script;
  }

  public List<Integer> getUsers() {
    return users;
  }

  public int getDuration() {
    return duration;
  }

  public String getTargetUrl() {
    return targetUrl;
  }

  public ScriptConfig getScript() {
    return script;
  }
}