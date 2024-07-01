package io.resiliencebench.resources.scenario;

import io.fabric8.generator.annotation.Nullable;

public class Connector {
  private String name;

  private String source;

  private String destination;

  @Nullable
  private Environment environment;

  @Nullable
  private IstioPattern istio;

  @Nullable
  private Fault fault;

  public Connector() { }

  public String getName() {
    return name;
  }

  public String getSource() {
    return source;
  }

  public String getDestination() {
    return destination;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public IstioPattern getIstio() {
    return istio;
  }

  public Fault getFault() {
    return fault;
  }

  public static class Builder {
    private String name;
    private String source;
    private String destination;
    private Environment environment;
    private IstioPattern istio;
    private Fault fault;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder source(String source) {
      this.source = source;
      return this;
    }

    public Builder destination(String destination) {
      this.destination = destination;
      return this;
    }

    public Builder environment(Environment environment) {
      this.environment = environment;
      return this;
    }

    public Builder istio(IstioPattern istio) {
      this.istio = istio;
      return this;
    }

    public Builder fault(Fault fault) {
      this.fault = fault;
      return this;
    }

    public Connector build() {
      Connector connector = new Connector();
      connector.name = name;
      connector.source = source;
      connector.destination = destination;
      connector.environment = environment;
      connector.istio = istio;
      connector.fault = fault;
      return connector;
    }
  }
}
