package br.unifor.ppgia.resiliencebench.execution.queue;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("eq")
@Plural("queues")
@Kind("Queue")
public class ExecutionQueue extends CustomResource<ExecutionQueueSpec, ExecutionQueueStatus> {
  ExecutionQueue() { }

  public ExecutionQueue(ExecutionQueueSpec spec, ObjectMeta meta) {
    this.spec = spec;
    this.setMetadata(meta);
  }
}
