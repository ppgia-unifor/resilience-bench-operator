package br.unifor.ppgia.resiliencebench.execution.scenario;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

import java.util.Arrays;

import static java.lang.String.*;
import static java.util.stream.Collectors.*;

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

  @Override
  public String toString() {
    var delimiter = ".";
    var keys = spec.getPatternConfig().keySet().stream().sorted(String::compareTo);
    var patternConfigSerialized = keys
            .map(k ->  k + "-" + spec.getPatternConfig().get(k))
            .collect(joining("."));
    if (patternConfigSerialized.isEmpty()) {
      patternConfigSerialized = "none";
    }
    return join(delimiter, Arrays.asList(
            spec.getSourceServiceName(), spec.getTargetServiceName(),
            patternConfigSerialized,
            spec.getWorkload().toString(),
            spec.getFault().toString()
    )).toLowerCase();
  }
}
