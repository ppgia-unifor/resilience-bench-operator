package io.resiliencebench.resources.scenario;

public class Connector {
  private String name;
  private Source source;
  private Target target;

  public Connector() {
  }

  public Connector(String name, Source source, Target target) {
    this.name = name;
    this.source = source;
    this.target = target;
  }

  public String getName() {
    return name;
  }

  public Source getSource() {
    return source;
  }

  public Target getTarget() {
    return target;
  }
}
