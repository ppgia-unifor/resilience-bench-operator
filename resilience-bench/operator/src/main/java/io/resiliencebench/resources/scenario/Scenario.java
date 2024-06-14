package io.resiliencebench.resources.scenario;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("sc")
@Plural("scenarios")
@Kind("Scenario")
public class Scenario extends CustomResource<ScenarioSpec, ScenarioStatus> implements Namespaced {

  public Scenario(ScenarioSpec spec) {
    setSpec(spec);
  }

  public Scenario() {
  }
}
