package io.resiliencebench.models.workload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.fabric8.generator.annotation.Default;
import io.resiliencebench.configuration.NameValueProperties;
import io.resiliencebench.models.workload.configuration.script.ScriptConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the specification for a Workload in the resilience benchmark.
 */
public class WorkloadSpec {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonPropertyDescription("List of users for the workload")
  private List<Integer> users = new ArrayList<>();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @JsonPropertyDescription("${workload.optionsDescription}")
  private NameValueProperties options = new NameValueProperties();

  @JsonPropertyDescription("The k6 container image to use")
  @Default("${workload.k6ContainerImage}")
  private String k6ContainerImage = "grafana/k6:latest";

  @JsonPropertyDescription("Configuration for the workload script")
  private ScriptConfig script;

  /**
   * Default constructor required for Jackson deserialization.
   */
  public WorkloadSpec() {
  }

  /**
   * Constructs a WorkloadSpec with the specified parameters.
   *
   * @param users            the list of users
   * @param options          the options for the k6 container
   * @param k6ContainerImage the k6 container image
   * @param script           the script configuration
   */
  public WorkloadSpec(List<Integer> users, NameValueProperties options, String k6ContainerImage, ScriptConfig script) {
    this.users = users == null ? new ArrayList<>() : users;
    this.options = options == null ? new NameValueProperties() : options;
    this.k6ContainerImage = k6ContainerImage == null ? "grafana/k6:latest" : k6ContainerImage;
    this.script = script;
  }

  /**
   * Returns the list of users.
   *
   * @return the list of users
   */
  public List<Integer> getUsers() {
    return users;
  }

  /**
   * Sets the list of users.
   *
   * @param users the list of users
   */
  public void setUsers(List<Integer> users) {
    this.users = users;
  }

  /**
   * Returns the options for the k6 container.
   *
   * @return the options for the k6 container
   */
  public NameValueProperties getOptions() {
    return options;
  }

  /**
   * Sets the options for the k6 container.
   *
   * @param options the options for the k6 container
   */
  public void setOptions(NameValueProperties options) {
    this.options = options;
  }

  /**
   * Returns the k6 container image.
   *
   * @return the k6 container image
   */
  public String getK6ContainerImage() {
    return k6ContainerImage;
  }

  /**
   * Sets the k6 container image.
   *
   * @param k6ContainerImage the k6 container image
   */
  public void setK6ContainerImage(String k6ContainerImage) {
    this.k6ContainerImage = k6ContainerImage;
  }

  /**
   * Returns the script configuration.
   *
   * @return the script configuration
   */
  public ScriptConfig getScript() {
    return script;
  }

  /**
   * Sets the script configuration.
   *
   * @param script the script configuration
   */
  public void setScript(ScriptConfig script) {
    this.script = script;
  }

  @Override
  public String toString() {
    return "WorkloadSpec{" +
            "users=" + users +
            ", options=" + options +
            ", k6ContainerImage='" + k6ContainerImage + '\'' +
            ", script=" + script +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WorkloadSpec that = (WorkloadSpec) o;
    return Objects.equals(users, that.users) &&
            Objects.equals(options, that.options) &&
            Objects.equals(k6ContainerImage, that.k6ContainerImage) &&
            Objects.equals(script, that.script);
  }

  @Override
  public int hashCode() {
    return Objects.hash(users, options, k6ContainerImage, script);
  }
}