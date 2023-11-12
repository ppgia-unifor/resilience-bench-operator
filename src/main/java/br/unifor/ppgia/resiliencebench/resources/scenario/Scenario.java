package br.unifor.ppgia.resiliencebench;

import io.fabric8.kubernetes.model.annotation.*;

@Group("resiliencebench.io")
@Version("v1")
@ShortNames("sc")
@Plural("scenarios")
@Kind("Scenario")
public class Scenario {

  private Object workload;

  private Object targetService;
  private Object sourceService;

  private Object fault;
  private Object patterParameters;
}
