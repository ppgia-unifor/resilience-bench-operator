package br.unifor.ppgia.resiliencebench.resources.benchmark;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkSpec {
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

  public int getRounds() {
    return rounds;
  }

  public List<Connection> getConnections() {
    return connections;
  }

  public String getWorkload() {
    return workload;
  }
}
