package br.unifor.ppgia.resiliencebench.support.fault;

import br.unifor.ppgia.resiliencebench.fault.AbortFault;
import br.unifor.ppgia.resiliencebench.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.fault.FaultTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FaultTemplateTest {

  @Test
  public void testToStringAbort() {
    var faultTemplate = new FaultTemplate(10, new AbortFault(500)) { };
    assertEquals("abort-500-10p", faultTemplate.toString());
  }

  @Test
  public void testToStringDelay() {
    var faultTemplate = new FaultTemplate(10, new DelayFault(100)) { };
    assertEquals("delay-100ms-10p", faultTemplate.toString());
  }
}