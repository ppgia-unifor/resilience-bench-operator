package io.resiliencebench.models.workload;

import io.resiliencebench.support.ConfigMapReference;

import java.util.Objects;

/**
 * Represents the configuration for a script, including a reference to a ConfigMap.
 */
public class ScriptConfig {

  private ConfigMapReference configMap;

  /**
   * Constructs a new ScriptConfig with the specified ConfigMap reference.
   *
   * @param configMap the reference to the ConfigMap containing the script configuration
   */
  public ScriptConfig(ConfigMapReference configMap) {
    this.configMap = configMap;
  }

  /**
   * Default constructor for ScriptConfig.
   */
  public ScriptConfig() {
    // Default constructor for deserialization
  }

  /**
   * Retrieves the ConfigMap reference for this ScriptConfig.
   *
   * @return the ConfigMap reference
   */
  public ConfigMapReference getConfigMap() {
    return configMap;
  }

  /**
   * Sets the ConfigMap reference for this ScriptConfig.
   *
   * @param configMap the ConfigMap reference to set
   */
  public void setConfigMap(ConfigMapReference configMap) {
    this.configMap = configMap;
  }

  /**
   * Provides a string representation of the ScriptConfig.
   *
   * @return a string representation of the ScriptConfig
   */
  @Override
  public String toString() {
    return "ScriptConfig{" +
            "configMap=" + configMap +
            '}';
  }

  /**
   * Checks whether this ScriptConfig is equal to another object.
   *
   * @param o the object to compare to
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ScriptConfig that = (ScriptConfig) o;
    return Objects.equals(configMap, that.configMap);
  }

  /**
   * Generates a hash code for this ScriptConfig.
   *
   * @return an integer hash code value
   */
  @Override
  public int hashCode() {
    return Objects.hash(configMap);
  }
}
