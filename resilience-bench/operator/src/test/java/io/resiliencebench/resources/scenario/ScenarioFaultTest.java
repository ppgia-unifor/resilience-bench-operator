package io.resiliencebench.resources.scenario;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class ScenarioFaultTest {

  @Test
  void testToJsonWithNonEmptyServices() {
    ScenarioFault fault = new ScenarioFault("provider1", 50, List.of("service1", "service2"));
    String expectedJson = "{\"provider\":\"provider1\",\"percentage\":50,\"services\":[\"service1\",\"service2\"]}";
    assertEquals(expectedJson, fault.toJson());
  }
}
