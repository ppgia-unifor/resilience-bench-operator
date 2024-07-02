package io.resiliencebench.models.scenario;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;
import io.resiliencebench.models.enums.ScenarioStatusEnum;

/**
 * Represents a Scenario Custom Resource in the Kubernetes cluster.
 */
@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("sc")
@Plural("scenarios")
@Kind("Scenario")
public class Scenario extends CustomResource<ScenarioSpec, ScenarioStatus> implements Namespaced {

  /**
   * Constructs a new Scenario with the specified spec.
   *
   * @param spec the specification of the scenario
   */
  public Scenario(ScenarioSpec spec) {
    this.setSpec(spec);
    this.setStatus(new ScenarioStatus(ScenarioStatusEnum.CREATED));
  }

  /**
   * Default constructor required for serialization/deserialization.
   */
  public Scenario() {
    // Default constructor for deserialization
    this.setStatus(new ScenarioStatus(ScenarioStatusEnum.CREATED));
  }

  /**
   * Returns the string representation of the Scenario.
   *
   * @return the string representation of the Scenario
   */
  @Override
  public String toString() {
    return "Scenario{" +
            "apiVersion='" + getApiVersion() + '\'' +
            ", kind='" + getKind() + '\'' +
            ", metadata=" + getMetadata() +
            ", spec=" + getSpec() +
            ", status=" + getStatus() +
            '}';
  }

  /**
   * Validates the Scenario object to ensure all required fields are set.
   *
   * @throws IllegalArgumentException if any required field is null or invalid
   */
  public void validate() {
    if (getSpec() == null) {
      throw new IllegalArgumentException("Spec cannot be null");
    }
  }

  /**
   * Sets the spec and validates the Scenario object.
   *
   * @param spec the specification to set
   * @throws IllegalArgumentException if the spec is null or invalid
   */
  @Override
  public void setSpec(ScenarioSpec spec) {
    super.setSpec(spec);
    validate();
  }
}
