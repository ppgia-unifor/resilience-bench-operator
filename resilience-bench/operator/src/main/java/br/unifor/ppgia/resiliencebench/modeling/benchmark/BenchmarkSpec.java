package br.unifor.ppgia.resiliencebench.modeling.benchmark;

import io.fabric8.generator.annotation.Max;
import io.fabric8.generator.annotation.Min;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkSpec {

  @Min(1) @Max(100)
  private int rounds;
  private String workload;
  private List<Connection> connections = new ArrayList<>();

  public BenchmarkSpec() {
  }

  public BenchmarkSpec(int rounds, String workload, List<Connection> connections) {
    this();
    this.rounds = rounds;
    this.workload = workload;
    this.connections = connections;
  }

  public List<Connection> getConnections() {
    return connections;
  }

  public String getWorkload() {
    return workload;
  }

  public int getRounds() {
    return rounds;
  }
}
