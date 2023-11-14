package br.unifor.ppgia.resiliencebench.resources.scenario;

import br.unifor.ppgia.resiliencebench.resources.fault.AbortFault;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.resources.fault.FaultTemplate;

public class ScenarioFaultTemplate extends FaultTemplate<Integer> {

  public ScenarioFaultTemplate() {
    super();
  }

  public ScenarioFaultTemplate(Integer percentage, DelayFault delay, AbortFault abort) {
    super(percentage, delay, abort);
  }
}
