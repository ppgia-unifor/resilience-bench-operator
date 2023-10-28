package br.unifor.ppgia.resiliencebench.resources.resilientservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

@Group("resiliencebench.io")
@Version("v1")
@ShortNames("rsvc")
@Plural("resilientservices")
@Kind("ResilientService")
public class ResilientService extends CustomResource<ResilientServiceSpec, ResilientServiceStatus> implements HasMetadata, Namespaced {

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
