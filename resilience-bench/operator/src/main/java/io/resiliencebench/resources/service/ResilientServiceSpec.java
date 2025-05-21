package io.resiliencebench.resources.service;

import java.util.List;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.LabelSelector;

public class ResilientServiceSpec {

  private LabelSelector selector;
  private List<EnvVar> envs;

  public LabelSelector getSelector() {
    return selector;
  }

  public void setSelector(LabelSelector selector) {
    this.selector = selector;
  }

  public List<EnvVar> getEnvs() {
    return envs;
  }

  public void setEnvs(List<EnvVar> envs) {
    this.envs = envs;
  }
}
