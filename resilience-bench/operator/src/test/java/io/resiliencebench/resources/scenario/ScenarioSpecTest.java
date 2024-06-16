package io.resiliencebench.resources.scenario;

import io.resiliencebench.resources.fault.DelayFault;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ScenarioSpecTest {

  @Test
  void testToJson() {
    var source = new Source("api-gateway", Map.of("backoffLimit", 100, "maxAttempts", 1));
    var delay = new DelayFault(1000);
    var fault = new ScenarioFaultTemplate(10, delay);
    var target = new Target("service-x", fault);
    var connector = new Connector("connector-1", source, target);

    var scenarioSpec = new ScenarioSpec(
            "scenario-1",
            new ScenarioWorkload("workload", 100),
            of(connector)
    );

    var actual = scenarioSpec.toJson();
    assertEquals("scenario-1", actual.getString("scenario"));
    assertEquals(100, actual.getJsonObject("workload").getInteger("users"));
    assertEquals("workload", actual.getJsonObject("workload").getString("workloadName"));

    var connectorObject = actual.getJsonArray("connectors").getJsonObject(0);
    assertEquals("connector-1", connectorObject.getString("name"));

    var sourceJson = connectorObject.getJsonObject("source");
    assertEquals("api-gateway", sourceJson.getString("serviceName"));
    assertEquals(100, sourceJson.getJsonObject("patternConfig").getInteger("backoffLimit"));

    var targetJson = connectorObject.getJsonObject("target");
    assertEquals("service-x", targetJson.getString("serviceName"));
    assertEquals(1000, targetJson.getJsonObject("fault").getJsonObject("delay").getInteger("duration"));

  }
}