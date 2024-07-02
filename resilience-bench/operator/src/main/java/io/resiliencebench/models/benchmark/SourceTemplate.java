package io.resiliencebench.models.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.resiliencebench.configuration.NameValueProperties;

/**
 * Represents a source template, including the service name and pattern configuration.
 */
public class SourceTemplate {

  private String service;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private NameValueProperties patternConfig;

  /**
   * Default constructor for SourceTemplate.
   * Initializes the pattern configuration to an empty NameValueProperties.
   */
  public SourceTemplate() {
    this.patternConfig = new NameValueProperties();
  }

  /**
   * Constructs a SourceTemplate with the specified service name and pattern configuration.
   *
   * @param service       the service name
   * @param patternConfig the pattern configuration
   */
  public SourceTemplate(String service, NameValueProperties patternConfig) {
    this.service = service;
    this.patternConfig = patternConfig;
  }

  /**
   * Returns the service name.
   *
   * @return the service name
   */
  public String getService() {
    return service;
  }

  /**
   * Sets the service name.
   *
   * @param service the service name to set
   */
  public void setService(String service) {
    this.service = service;
  }

  /**
   * Returns the pattern configuration.
   *
   * @return the pattern configuration
   */
  public NameValueProperties getPatternConfig() {
    return patternConfig;
  }

  /**
   * Sets the pattern configuration.
   *
   * @param patternConfig the pattern configuration to set
   */
  public void setPatternConfig(NameValueProperties patternConfig) {
    this.patternConfig = patternConfig;
  }

  @Override
  public String toString() {
    return "SourceTemplate{" +
            "service='" + service + '\'' +
            ", patternConfig=" + patternConfig +
            '}';
  }
}
