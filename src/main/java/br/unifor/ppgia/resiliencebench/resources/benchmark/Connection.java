package br.unifor.ppgia.resiliencebench.resources.benchmark;

public class Connection {

  private String name;
  private Source source;
  private Target target;

  public Connection() {
  }

  public Connection(String name, Source source, Target target) {
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
