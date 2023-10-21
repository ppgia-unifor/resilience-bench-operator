package br.unifor.ppgia.resiliencebench.resources.benchmark;

import java.util.ArrayList;
import java.util.List;

public class ScenarioTemplate {

  private String name;
  private List<ResilientServiceTemplate> services = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ResilientServiceTemplate> getServices() {
    return services;
  }

  public void setServices(List<ResilientServiceTemplate> resilientServices) {
    this.services = resilientServices;
  }
}
