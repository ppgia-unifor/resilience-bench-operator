package io.resiliencebench.models.queue;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

/**
 * Represents an execution queue.
 */
@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("eq")
@Plural("queues")
@Kind("Queue")
public class ExecutionQueue extends CustomResource<ExecutionQueueSpec, ExecutionQueueStatus> implements Namespaced {

  /**
   * Default constructor.
   */
  public ExecutionQueue() {
    // Default constructor required for deserialization
  }

  /**
   * Constructs a new ExecutionQueue with the specified spec and metadata.
   *
   * @param spec the specification of the execution queue
   * @param meta the metadata of the execution queue
   */
  public ExecutionQueue(ExecutionQueueSpec spec, ObjectMeta meta) {
    this.setSpec(spec);
    this.setMetadata(meta);
  }

  /**
   * Sets the specification of the execution queue.
   *
   * @param spec the specification to set
   */
  public void setSpec(ExecutionQueueSpec spec) {
    this.spec = spec;
  }

  /**
   * Returns the specification of the execution queue.
   *
   * @return the specification of the execution queue
   */
  public ExecutionQueueSpec getSpec() {
    return spec;
  }

  /**
   * Sets the status of the execution queue.
   *
   * @param status the status to set
   */
  public void setStatus(ExecutionQueueStatus status) {
    this.status = status;
  }

  /**
   * Returns the status of the execution queue.
   *
   * @return the status of the execution queue
   */
  public ExecutionQueueStatus getStatus() {
    return status;
  }
}
