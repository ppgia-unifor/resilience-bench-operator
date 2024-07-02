package io.resiliencebench.models.benchmark;

import io.resiliencebench.models.fault.AbortFault;
import io.resiliencebench.models.fault.DelayFault;
import io.resiliencebench.models.fault.FaultTemplate;

import java.util.List;

/**
 * Represents a benchmark fault template, extending the generic FaultTemplate class.
 * This class is used to define fault injection templates specific to benchmarks, including
 * delay faults and abort faults with specific percentages.
 */
public class BenchmarkFaultTemplate extends FaultTemplate<List<Integer>> {

  /**
   * Default constructor for BenchmarkFaultTemplate.
   */
  public BenchmarkFaultTemplate() {
    // Default constructor
  }

  /**
   * Constructs a BenchmarkFaultTemplate with a specified percentage and delay fault.
   *
   * @param percentage the percentage of fault injection
   * @param delay      the delay fault to inject
   */
  public BenchmarkFaultTemplate(List<Integer> percentage, DelayFault delay) {
    super(percentage, delay);
  }

  /**
   * Constructs a BenchmarkFaultTemplate with a specified percentage and abort fault.
   *
   * @param percentage the percentage of fault injection
   * @param abort      the abort fault to inject
   */
  public BenchmarkFaultTemplate(List<Integer> percentage, AbortFault abort) {
    super(percentage, abort);
  }
}
