package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.fabric8.generator.annotation.Nullable;
import io.resiliencebench.resources.NameValueProperties;

public class ServiceTemplate {

  @JsonPropertyDescription("The name of the ResilientService to apply the environments to")
  private String name;

  @Nullable
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonPropertyDescription("The set of environments to apply to the ResilientService")
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
