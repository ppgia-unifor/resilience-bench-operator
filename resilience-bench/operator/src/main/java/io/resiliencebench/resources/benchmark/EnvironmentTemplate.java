package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.resiliencebench.resources.NameValueProperties;

public class EnvironmentTemplate {

  private String applyTo;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private NameValueProperties envs;

  public EnvironmentTemplate() {
  }

  public EnvironmentTemplate(String applyTo, NameValueProperties envs) {
    this.applyTo = applyTo;
    this.envs = envs;
  }

  public String getApplyTo() {
    return applyTo;
  }

  public NameValueProperties getEnvs() {
    return envs;
  }
}
