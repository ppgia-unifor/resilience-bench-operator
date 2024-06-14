package io.resiliencebench.resources.benchmark;

public class ConnectorTemplate {

  private String name;
  private SourceTemplate source;
  private TargetTemplate target;

  public ConnectorTemplate() {
  }

  public ConnectorTemplate(String name, SourceTemplate source, TargetTemplate target) {
    this.name = name;
    this.source = source;
    this.target = target;
  }

  public String getName() {
    return name;
  }

  public SourceTemplate getSource() {
    return source;
  }

  public TargetTemplate getTarget() {
    return target;
  }
}
