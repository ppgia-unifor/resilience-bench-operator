package br.unifor.ppgia.resiliencebench.resources.scenario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScenarioTest {

  @Test
  public void should_set_meta_name() {
    var spec = new ScenarioSpec();
    spec.setWorkload(new Workload(10, 100, "", "", ""));
    spec.setName("all-retry");
    spec.setRound(10);
    var scenario = new Scenario(spec);
    assertEquals(spec.getId(), scenario.getMetadata().getName());
  }
}