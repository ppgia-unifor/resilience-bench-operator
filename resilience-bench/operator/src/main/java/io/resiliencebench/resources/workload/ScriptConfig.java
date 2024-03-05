package io.resiliencebench.resources.workload;

import io.resiliencebench.support.ConfigMapReference;

public class ScriptConfig {

  public ScriptConfig(ConfigMapReference configMap) {
    this.configMap = configMap;
  }

  public ScriptConfig() {
  }

  private ConfigMapReference configMap;

  public ConfigMapReference getConfigMap() {
    return configMap;
  }
}
