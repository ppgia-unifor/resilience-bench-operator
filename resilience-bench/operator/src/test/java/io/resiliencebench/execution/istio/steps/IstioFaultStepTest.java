package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionAbortHttpStatus;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionDelayFixedDelay;
import io.resiliencebench.execution.steps.istio.IstioFaultStep;
import io.resiliencebench.resources.fault.AbortFault;
import io.resiliencebench.resources.fault.DelayFault;
import io.resiliencebench.resources.scenario.Fault;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IstioFaultStepTest {

  @Test
  void testConfigureFaultWithDelay() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new Fault(10, new DelayFault(1000));
    var fault = istioFaultStep.createFault(faultTemplate).get();
    assertEquals(10.0d, fault.getDelay().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionDelayFixedDelay("1000ms"), fault.getDelay().getHttpDelayType());
    assertNull(fault.getAbort());
  }

  @Test
  void testConfigureFaultWithAbort() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new Fault(10, new AbortFault(500));
    var fault = istioFaultStep.createFault(faultTemplate).get();
    assertEquals(10.0d, fault.getAbort().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionAbortHttpStatus(500), fault.getAbort().getErrorType());
    assertNull(fault.getDelay());
  }

  @Test
  void testConfigureFaultWithoutFault() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new Fault();
    var fault = istioFaultStep.createFault(faultTemplate);
    assertEquals(Optional.empty(), fault);
  }
}
