package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.fabric8.generator.annotation.Nullable;
import io.resiliencebench.resources.NameValueProperties;

public class ServiceTemplate {

  private String name;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private NameValueProperties envs;

  public ServiceTemplate() {
  }

  public ServiceTemplate(String name, NameValueProperties envs) {
    this(name);
    this.envs = envs;
  }

  public ServiceTemplate(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public NameValueProperties getEnvs() {
    return envs;
  }
}
