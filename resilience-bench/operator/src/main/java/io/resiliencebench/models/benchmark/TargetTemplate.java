package io.resiliencebench.models.benchmark;

/**
 * Represents a target template, including the service name and fault template.
 */
public class TargetTemplate {

  private String service;
  private BenchmarkFaultTemplate fault;

  /**
   * Default constructor for TargetTemplate.
   * Initializes the target template with default values.
   */
  public TargetTemplate() {
    // Default constructor
  }

  /**
   * Constructs a TargetTemplate with the specified service name and fault template.
   *
   * @param service the service name
   * @param fault   the fault template
   */
  public TargetTemplate(String service, BenchmarkFaultTemplate fault) {
    this.service = service;
    this.fault = fault;
  }

  /**
   * Returns the fault template.
   *
   * @return the fault template
   */
  public BenchmarkFaultTemplate getFault() {
    return fault;
  }

  /**
   * Sets the fault template.
   *
   * @param fault the fault template to set
   */
  public void setFault(BenchmarkFaultTemplate fault) {
    this.fault = fault;
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

  @Override
  public String toString() {
    return "TargetTemplate{" +
            "service='" + service + '\'' +
            ", fault=" + fault +
            '}';
  }
}
