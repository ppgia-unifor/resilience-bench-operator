package br.unifor.ppgia.resiliencebench.resources.benchmark;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkSpec {
  private int rounds;
  private String workload;
  private List<Connection> connections = new ArrayList<>();

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
