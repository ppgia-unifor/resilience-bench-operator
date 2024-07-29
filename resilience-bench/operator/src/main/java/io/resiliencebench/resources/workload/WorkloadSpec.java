package io.resiliencebench.resources.workload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.fabric8.generator.annotation.Default;
import io.resiliencebench.resources.NameValueProperties;

import java.util.ArrayList;
import java.util.List;

public class WorkloadSpec {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<Integer> users = new ArrayList<>();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonPropertyDescription("Environment variables to set in the k6 container. See here for more information: https://k6.io/docs/using-k6/k6-options/reference/")
  private final NameValueProperties options = new NameValueProperties();


  @JsonPropertyDescription("The k6 container image to use")
  @Default("grafana/k6:latest")
  private String k6ContainerImage;

  private ScriptConfig script;

  public WorkloadSpec() {
  }

  public WorkloadSpec(List<Integer> users,
                      ScriptConfig script) {
    this();
    this.users = users;
    this.script = script;
  }

  public List<Integer> getUsers() {
    return users;
  }

  public NameValueProperties getOptions() {
    return options;
  }

  public ScriptConfig getScript() {
    return script;
  }

  public String getK6ContainerImage() {
    return k6ContainerImage;
  }
}
