package io.resiliencebench.resources.workload;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("wl")
@Plural("workloads")
@Kind("Workload")
public class Workload extends CustomResource<WorkloadSpec, WorkloadStatus> implements Namespaced {

  @JsonProperty("metadata")
  private ObjectMeta metadata;

  @Override
  public ObjectMeta getMetadata() {
    return metadata;
  }

  @Override
  public void setMetadata(ObjectMeta metadata) {
    this.metadata = metadata;
  }
}
