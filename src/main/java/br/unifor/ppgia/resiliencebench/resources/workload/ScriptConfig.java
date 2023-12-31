package br.unifor.ppgia.resiliencebench.resources.workload;

import br.unifor.ppgia.resiliencebench.resources.ConfigMapReference;

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
