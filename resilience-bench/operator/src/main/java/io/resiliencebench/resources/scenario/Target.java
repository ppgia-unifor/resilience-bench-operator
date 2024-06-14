package io.resiliencebench.resources.scenario;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.resiliencebench.resources.scenario.ScenarioFaultTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

public class Target {

  private ScenarioFaultTemplate fault;
  private String serviceName;

  public Target() {
  }

  public Target(
          String serviceName,
          ScenarioFaultTemplate fault
  ) {
    this.serviceName = serviceName;
    this.fault = fault;
  }

  public ScenarioFaultTemplate getFault() {
    return fault;
  }

  public String getServiceName() {
    return serviceName;
  }
}
