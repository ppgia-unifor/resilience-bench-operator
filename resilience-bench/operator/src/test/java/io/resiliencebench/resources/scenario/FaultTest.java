package io.resiliencebench.resources.scenario;

import io.resiliencebench.resources.fault.AbortFault;
import io.resiliencebench.resources.fault.DelayFault;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FaultTest {

  @Test
  public void fromWithDelay() {
    var fault = Fault.create(10, new DelayFault(1000), null);
    assertEquals(10, fault.getPercentage());
    assertEquals(new DelayFault(1000), fault.getDelay());
    assertNull(fault.getAbort());
  }

  @Test
  public void fromWithoutDelay() {
    var fault = Fault.create(10, null, null);
    assertNull(fault);
  }

  @Test
  public void fromWithAbort() {
    var fault = Fault.create(10, null, new AbortFault(500));
    assertEquals(10, fault.getPercentage());
    assertEquals(new AbortFault(500), fault.getAbort());
    assertNull(fault.getDelay());
  }

  @Test
  public void fromWithoutAny() {
    var fault = Fault.create(10, null, null);
    assertNull(fault);
  }
}