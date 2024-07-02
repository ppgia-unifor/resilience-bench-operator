package io.resiliencebench.resources.benchmark;

import io.resiliencebench.resources.fault.AbortFault;
import io.resiliencebench.resources.fault.DelayFault;
import io.resiliencebench.resources.fault.FaultTemplate;

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
