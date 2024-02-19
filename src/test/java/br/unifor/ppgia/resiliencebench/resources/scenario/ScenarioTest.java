package br.unifor.ppgia.resiliencebench.resources.scenario;

import br.unifor.ppgia.resiliencebench.resources.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.resources.execution.scenario.ScenarioFaultTemplate;
import br.unifor.ppgia.resiliencebench.resources.execution.scenario.ScenarioSpec;
import br.unifor.ppgia.resiliencebench.resources.execution.scenario.ScenarioWorkload;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScenarioTest {

  private final String regex = "[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*";

  @Test
  void test_name_with_all_attributes() {
    var spec = new ScenarioSpec(
            "target-service-name",
            "source-service-name",
            Map.of("za", 1.1, "maxAttempts", 10, "perRetryTimeout", 100),
            new ScenarioWorkload("workloadName", 100),
            new ScenarioFaultTemplate(25, new DelayFault(100))
    );
    var scenario = new Scenario(spec);
    assertEquals("source-service-name.target-service-name.maxattempts-10.perretrytimeout-100.za-1.1.workloadname-100.delay-100ms-25p", scenario.toString());
    assertTrue(scenario.toString().matches(regex));
  }

  @Test
  void test_name_with_no_params() {
    var spec = new ScenarioSpec(
            "target-service-name",
            "source-service-name",
            Map.of(),
            new ScenarioWorkload("workloadName", 100),
            new ScenarioFaultTemplate(25, new DelayFault(100))
    );
    var scenario = new Scenario(spec);
    assertEquals("source-service-name.target-service-name.none.workloadname-100.delay-100ms-25p", scenario.toString());
    assertTrue(scenario.toString().matches(regex));
  }
}