package br.unifor.ppgia.resiliencebench.resources.scenario;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScenarioSpec {
  
  private String targetServiceName;
  private String sourceServiceName;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, Object> patternConfig = new LinkedHashMap<>();

  private ScenarioWorkload workload;
  private ScenarioFaultTemplate fault;

  public ScenarioSpec(
          String targetServiceName,
          String sourceServiceName,
          Map<String, Object> patternConfig,
          ScenarioWorkload workload,
          ScenarioFaultTemplate fault
  ) {
    this.targetServiceName = targetServiceName;
    this.sourceServiceName = sourceServiceName;
    this.workload = workload;
    this.fault = fault;
    this.patternConfig = patternConfig;
  }

  public ScenarioSpec() {
  }

  public String getTargetServiceName() {
    return targetServiceName;
  }

  public String getSourceServiceName() {
    return sourceServiceName;
  }

  public ScenarioWorkload getWorkload() {
    return workload;
  }

  public ScenarioFaultTemplate getFault() {
    return fault;
  }

  public Map<String, Object> getPatternConfig() {
    return patternConfig;
  }
}
