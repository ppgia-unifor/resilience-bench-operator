package io.resiliencebench.resources.scenario;

import io.resiliencebench.resources.fault.AbortFault;
import io.resiliencebench.resources.fault.DelayFault;
import io.resiliencebench.resources.fault.FaultTemplate;

public class ScenarioFaultTemplate extends FaultTemplate<Integer> {

  public ScenarioFaultTemplate() {
    super();
  }

  public ScenarioFaultTemplate(Integer percentage, AbortFault abort) {
    super(percentage, abort);
  }

  public ScenarioFaultTemplate(Integer percentage, DelayFault delay) {
    super(percentage, delay);
  }

  public static ScenarioFaultTemplate create(Integer percentage, DelayFault delay, AbortFault abort) {
    if (delay != null) {
      return new ScenarioFaultTemplate(percentage, delay);
    } else if (abort != null) {
      return new ScenarioFaultTemplate(percentage, abort);
    } else {
      return null;
    }
  }
}
