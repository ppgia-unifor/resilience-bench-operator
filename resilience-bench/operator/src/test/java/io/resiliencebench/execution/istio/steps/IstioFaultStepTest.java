package io.resiliencebench.execution.istio.steps;

import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionAbortHttpStatus;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionDelayFixedDelay;
import io.resiliencebench.models.fault.AbortFault;
import io.resiliencebench.models.fault.DelayFault;
import io.resiliencebench.models.scenario.ScenarioFaultTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IstioFaultStepTest {

  @Test
  @DisplayName("Test configuring fault with delay")
  void testConfigureFaultWithDelay() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new ScenarioFaultTemplate(10, new DelayFault(1000));

    var fault = istioFaultStep.createFault(faultTemplate).orElseThrow();

    assertEquals(10.0d, fault.getDelay().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionDelayFixedDelay("1000ms"), fault.getDelay().getHttpDelayType());
    assertNull(fault.getAbort());
  }

  @Test
  @DisplayName("Test configuring fault with abort")
  void testConfigureFaultWithAbort() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new ScenarioFaultTemplate(10, new AbortFault(500));

    var fault = istioFaultStep.createFault(faultTemplate).orElseThrow();

    assertEquals(10.0d, fault.getAbort().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionAbortHttpStatus(500), fault.getAbort().getErrorType());
    assertNull(fault.getDelay());
  }

  @Test
  @DisplayName("Test configuring fault without fault")
  void testConfigureFaultWithoutFault() {
    var istioFaultStep = new IstioFaultStep(null, null, null);
    var faultTemplate = new ScenarioFaultTemplate();

    var fault = istioFaultStep.createFault(faultTemplate);

    assertEquals(Optional.empty(), fault);
  }
}
