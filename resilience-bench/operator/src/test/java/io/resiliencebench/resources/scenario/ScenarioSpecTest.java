package io.resiliencebench.resources.scenario;

import io.resiliencebench.resources.fault.DelayFault;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScenarioSpecTest {

  @Test
  void testToConnectorsInJson_withEmptyConnectors() {
    var scenarioSpec = new ScenarioSpec("Test Scenario", null, List.of());
    var result = scenarioSpec.toConnectorsInJson();
    assertEquals(0, result.size(), "JsonArray should be empty when there are no connectors.");
  }

  @Test
  void testToConnectorsInJson_withOneConnector() {
    var connector = new Connector.Builder()
            .source(new Service("Test Source"))
            .destination(new Service("Test Destination"))
            .fault(new Fault(100, new DelayFault(10)))
            .name("Test Connector").build();
    var scenarioSpec = new ScenarioSpec("Test Scenario", null, List.of(connector));
    var result = scenarioSpec.toConnectorsInJson();
    assertEquals(1, result.size(), "JsonArray should have one element when there is one connector.");
  }
}