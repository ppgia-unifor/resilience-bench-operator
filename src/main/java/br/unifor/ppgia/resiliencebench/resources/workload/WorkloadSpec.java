package br.unifor.ppgia.resiliencebench.resources.workload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.fabric8.generator.annotation.Min;
import io.fabric8.generator.annotation.Pattern;

import java.util.ArrayList;
import java.util.List;

public class WorkloadSpec {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> users = new ArrayList<>();
  @Min(1)
  @JsonPropertyDescription("Workload duration in milliseconds")
  private int duration;
  @Pattern("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$")
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
