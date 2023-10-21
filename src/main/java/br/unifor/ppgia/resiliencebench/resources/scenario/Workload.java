package br.unifor.ppgia.resiliencebench.resources.scenario;

public class Workload {

  private Integer users;
  private Integer duration;
  private String targetUrl;
  private String locustFile;
  private String locustHost;

  public Workload() {

  }

  public Workload(Integer users, Integer duration, String targetUrl, String locustFile, String locustHost) {
    this.users = users;
    this.duration = duration;
    this.targetUrl = targetUrl;
    this.locustFile = locustFile;
    this.locustHost = locustHost;
  }

  public Integer getUsers() {
    return users;
  }

  public Integer getDuration() {
    return duration;
  }

  public String getTargetUrl() {
    return targetUrl;
  }

  public String getLocustFile() {
    return locustFile;
  }

  public String getLocustHost() {
    return locustHost;
  }
}
