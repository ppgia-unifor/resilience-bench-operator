package br.unifor.ppgia.resiliencebench.resources.scenario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScenarioSpecTest {

  @Test
  public void should_generate_id() {
    var spec = new ScenarioSpec();
    spec.setWorkload(new Workload(10, 100, "", "", ""));
    spec.setName("all-retry");
    spec.setRound(10);
    assertTrue(spec.getId().startsWith("all-retry.r10.w10"));
  }
}