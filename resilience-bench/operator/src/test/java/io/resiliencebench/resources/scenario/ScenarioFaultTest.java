package io.resiliencebench.resources.scenario;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

class ScenarioFaultTest {

  @Test
  void testToJsonWithNonEmptyServices() {
    var fault = new ScenarioFault("provider1", 50, List.of("service1", "service2"));
    var expectedJson = JsonObject.of("fault_provider", "provider1")
              .put("fault_percentage", 50)
              .put("fault_services", List.of("service1", "service2"));
    assertEquals(expectedJson, fault.toJson());
  }
}
