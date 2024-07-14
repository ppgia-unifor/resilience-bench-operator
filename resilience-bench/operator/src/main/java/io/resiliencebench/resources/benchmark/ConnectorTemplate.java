package io.resiliencebench.resources.benchmark;

import io.fabric8.generator.annotation.Nullable;

public class ConnectorTemplate {

  private String name;
  private ServiceTemplate source;
  private ServiceTemplate destination;

  @Nullable
  private BenchmarkFaultTemplate fault;
  @Nullable
  private PatternTemplate pattern;

  public ConnectorTemplate() {
  }

  public ConnectorTemplate(String name, ServiceTemplate source, ServiceTemplate destination, BenchmarkFaultTemplate fault, PatternTemplate pattern) {
    this.name = name;
    this.source = source;
    this.destination = destination;
    this.fault = fault;
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public ServiceTemplate getSource() {
    return source;
  }

  public ServiceTemplate getDestination() {
    return destination;
  }

  public BenchmarkFaultTemplate getFault() {
    return fault;
  }

  public PatternTemplate getPattern() {
    return pattern;
  }

}
