package br.unifor.ppgia.resiliencebench.resources.scenario;

import br.unifor.ppgia.resiliencebench.resources.execution.scenario.ScenarioFaultTemplate;
import br.unifor.ppgia.resiliencebench.resources.fault.AbortFault;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioFaultTemplateTest {

  @Test
  public void fromWithDelay() {
    var fault = ScenarioFaultTemplate.create(10, new DelayFault(1000), null);
    assertEquals(10, fault.getPercentage());
    assertEquals(new DelayFault(1000), fault.getDelay());
    assertNull(fault.getAbort());
  }

  @Test
  public void fromWithoutDelay() {
    var fault = ScenarioFaultTemplate.create(10, null, null);
    assertNull(fault);
  }

  @Test
  public void fromWithAbort() {
    var fault = ScenarioFaultTemplate.create(10, null, new AbortFault(500));
    assertEquals(10, fault.getPercentage());
    assertEquals(new AbortFault(500), fault.getAbort());
    assertNull(fault.getDelay());
  }

  @Test
  public void fromWithoutAny() {
    var fault = ScenarioFaultTemplate.create(10, null, null);
    assertNull(fault);
  }
}