package io.resiliencebench.models.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

import java.util.Objects;

/**
 * Represents a ResilientService Custom Resource in the Kubernetes cluster.
 */
@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("rsvc")
@Plural("resilientservices")
@Kind("ResilientService")
public class ResilientService extends CustomResource<ResilientServiceSpec, ResilientServiceStatus> implements Namespaced {

  @JsonProperty("metadata")
  private ObjectMeta metadata;

  /**
   * Returns the metadata of the ResilientService.
   *
   * @return the metadata of the ResilientService
   */
  @Override
  public ObjectMeta getMetadata() {
    return metadata;
  }

  /**
   * Sets the metadata for the ResilientService.
   *
   * @param metadata the metadata to set
   */
  @Override
  public void setMetadata(ObjectMeta metadata) {
    this.metadata = metadata;
  }

  /**
   * Returns a string representation of the ResilientService.
   *
   * @return a string representation of the ResilientService
   */
  @Override
  public String toString() {
    return "ResilientService{" +
            "metadata=" + metadata +
            ", spec=" + getSpec() +
            ", status=" + getStatus() +
            '}';
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare
   * @return true if this object is the same as the obj argument; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResilientService that = (ResilientService) o;
    return Objects.equals(metadata, that.metadata) &&
            Objects.equals(getSpec(), that.getSpec()) &&
            Objects.equals(getStatus(), that.getStatus());
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(metadata, getSpec(), getStatus());
  }

  /**
   * Updates the status of the resilient service.
   *
   * @param state   the new state of the resilient service
   * @param message the message related to the state
   */
  public void updateStatus(String state, String message) {
    setStatus(new ResilientServiceStatus(state, message));
  }
}
