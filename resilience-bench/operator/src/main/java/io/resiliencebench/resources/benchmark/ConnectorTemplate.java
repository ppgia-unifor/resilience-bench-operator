package io.resiliencebench.resources.benchmark;

import io.fabric8.generator.annotation.Nullable;

public class ConnectorTemplate {

  private String name;
  private String source;
  private String destination;

  @Nullable
  private BenchmarkFaultTemplate fault;
  @Nullable
  private PatternTemplate pattern;

  @Nullable
  private EnvironmentTemplate environment;

  public ConnectorTemplate() {
  }

  public ConnectorTemplate(String name, String source, String destination, BenchmarkFaultTemplate fault, PatternTemplate pattern) {
    this.name = name;
    this.source = source;
    this.destination = destination;
    this.fault = fault;
    this.pattern = pattern;
  }

  public String getName() {
    return name;
  }

  public String getSource() {
    return source;
  }

  public String getDestination() {
    return destination;
  }

  public BenchmarkFaultTemplate getFault() {
    return fault;
  }

  public PatternTemplate getPattern() {
    return pattern;
  }

  public EnvironmentTemplate getEnvironment() {
    return environment;
  }
}
