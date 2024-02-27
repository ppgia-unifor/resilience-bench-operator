package br.unifor.ppgia.resiliencebench.resources.benchmark;

import br.unifor.ppgia.resiliencebench.resources.fault.AbortFault;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.resources.fault.FaultTemplate;

import java.util.List;

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
