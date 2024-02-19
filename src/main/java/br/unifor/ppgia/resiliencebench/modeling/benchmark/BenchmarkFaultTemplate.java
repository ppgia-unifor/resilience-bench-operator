package br.unifor.ppgia.resiliencebench.modeling.benchmark;

import java.util.List;

import br.unifor.ppgia.resiliencebench.fault.AbortFault;
import br.unifor.ppgia.resiliencebench.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.fault.FaultTemplate;

public class BenchmarkFaultTemplate extends FaultTemplate<List<Integer>> {

  public BenchmarkFaultTemplate() {
  }

  public BenchmarkFaultTemplate(List<Integer> percentage, DelayFault delay) {
    super(percentage, delay);
  }

  public BenchmarkFaultTemplate(List<Integer> percentage, AbortFault abort) {
    super(percentage, abort);
  }
}
