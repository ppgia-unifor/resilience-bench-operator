package br.unifor.ppgia.resiliencebench.resources.scenario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScenarioWorkloadTest {

  @Test
  public void test_name_with_alpha() {
    var workload = new ScenarioWorkload("theName", 10);
    assertEquals("theName-10", workload.toString());
  }

  @Test
  public void test_name_with_alphanumerics() {
    var workload = new ScenarioWorkload("theNam3", 10);
    assertEquals("theNam3-10", workload.toString());
  }
}