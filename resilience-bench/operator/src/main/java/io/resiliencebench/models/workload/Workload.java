package io.resiliencebench.models.workload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;
import io.resiliencebench.models.enums.ScenarioStatusEnum;
import io.resiliencebench.models.enums.WorkloadStatusMessages;
import io.resiliencebench.models.workload.status.WorkloadStatus;

import java.util.Objects;

/**
 * Represents a Workload Custom Resource in the Kubernetes cluster.
 * This class encapsulates the metadata, specification, and status of a workload resource.
 */
@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("wl")
@Plural("workloads")
@Kind("Workload")
public class Workload extends CustomResource<WorkloadSpec, WorkloadStatus> implements Namespaced {

  @JsonProperty("metadata")
  private ObjectMeta metadata;

  /**
   * Constructs a new Workload with the specified spec and initializes the status.
   *
   * @param spec the specification of the workload
   */
  public Workload(WorkloadSpec spec) {
    this.setSpec(spec);
    this.setStatus(WorkloadStatus.newBuilder()
            .withStatus(ScenarioStatusEnum.PENDING)
            .withMessage(WorkloadStatusMessages.PENDING.getMessage())
            .build());
  }

  /**
   * Default constructor required for serialization/deserialization.
   */
  public Workload() {
    // Default constructor for deserialization
    this.setStatus(WorkloadStatus.newBuilder()
            .withStatus(ScenarioStatusEnum.PENDING)
            .withMessage(WorkloadStatusMessages.PENDING.getMessage())
            .build());
  }

  /**
   * Retrieves the metadata of the Workload.
   *
   * @return the ObjectMeta instance representing the metadata of the Workload.
   */
  @Override
  public ObjectMeta getMetadata() {
    return metadata;
  }

  /**
   * Sets the metadata for the Workload.
   *
   * @param metadata the ObjectMeta instance to set.
   */
  @Override
  public void setMetadata(ObjectMeta metadata) {
    this.metadata = metadata;
  }

  /**
   * Updates the status of the workload.
   *
   * @param status the new status to set
   * @param message the message related to the status
   */
  public void updateStatus(ScenarioStatusEnum status, WorkloadStatusMessages message) {
    WorkloadStatus newStatus = WorkloadStatus.newBuilder()
            .withStatus(status)
            .withMessage(message.getMessage())
            .build();
    this.setStatus(newStatus);
  }

  /**
   * Provides a string representation of the Workload.
   *
   * @return a string representation of the Workload instance.
   */
  @Override
  public String toString() {
    return "Workload{" +
            "metadata=" + metadata +
            ", spec=" + getSpec() +
            ", status=" + getStatus() +
            '}';
  }

  /**
   * Checks whether this Workload is equal to another object.
   *
   * @param o the object to compare to.
   * @return true if the objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Workload workload = (Workload) o;
    return Objects.equals(metadata, workload.metadata) &&
            Objects.equals(getSpec(), workload.getSpec()) &&
            Objects.equals(getStatus(), workload.getStatus());
  }

  /**
   * Generates a hash code for this Workload.
   *
   * @return an integer hash code value.
   */
  @Override
  public int hashCode() {
    return Objects.hash(metadata, getSpec(), getStatus());
  }
}
