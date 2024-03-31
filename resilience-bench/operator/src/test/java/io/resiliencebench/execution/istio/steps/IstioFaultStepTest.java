package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionAbortHttpStatus;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionDelayFixedDelay;
import io.resiliencebench.resources.fault.AbortFault;
import io.resiliencebench.resources.fault.DelayFault;
import io.resiliencebench.resources.scenario.ScenarioFaultTemplate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IstioFaultStepTest {

  @Test
  void testConfigureFaultWithDelay() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new ScenarioFaultTemplate(10, new DelayFault(1000));
    var fault = istioFaultStep.configureFault(faultTemplate);
    assertEquals(10.0d, fault.getDelay().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionDelayFixedDelay("1000ms"), fault.getDelay().getHttpDelayType());
    assertNull(fault.getAbort());
  }

  @Test
  void testConfigureFaultWithAbort() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new ScenarioFaultTemplate(10, new AbortFault(500));
    var fault = istioFaultStep.configureFault(faultTemplate);
    assertEquals(10.0d, fault.getAbort().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionAbortHttpStatus(500), fault.getAbort().getErrorType());
    assertNull(fault.getDelay());
  }

  @Test
  void testConfigureFaultWithoutFault() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new ScenarioFaultTemplate();
    var fault = istioFaultStep.configureFault(faultTemplate);
    assertNull(fault);
  }
}
