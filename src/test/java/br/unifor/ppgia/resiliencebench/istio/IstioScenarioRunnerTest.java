package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.fault.AbortFault;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioFaultTemplate;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionAbortHttpStatus;
import io.fabric8.istio.api.networking.v1beta1.HTTPFaultInjectionDelayFixedDelay;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IstioScenarioRunnerTest {

  @Test
  void configureRetryPatternWithAttemptsAndPerTryTimeout() {
    var runner = new IstioScenarioRunner(null, null);
    Map<String, Object> patternConfig = Map.of("attempts", 3, "perTryTimeout", 10);
    var retry = runner.configureRetryPattern(patternConfig);
    assertEquals(3, retry.getAttempts());
    assertEquals("10ms", retry.getPerTryTimeout());
  }

  @Test
  void configureFaultWithDelayConfig() {
    var runner = new IstioScenarioRunner(null, null);
    var faultConfig = new ScenarioFaultTemplate(10, new DelayFault(100));
    var fault = runner.configureFault(faultConfig);
    assertEquals(10d, fault.getDelay().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionDelayFixedDelay("100ms"), fault.getDelay().getHttpDelayType());
  }
  @Test
  void configureFaultWithAbortConfig() {
    var runner = new IstioScenarioRunner(null, null);
    var faultConfig = new ScenarioFaultTemplate(10, new AbortFault(500));
    var fault = runner.configureFault(faultConfig);
    assertEquals(10d, fault.getAbort().getPercentage().getValue());
    assertEquals(new HTTPFaultInjectionAbortHttpStatus(500), fault.getAbort().getErrorType());
  }
}