package br.unifor.ppgia.resiliencebench.resources.service;

import br.unifor.ppgia.resiliencebench.resources.benchmark.FaultTemplate;
import io.fabric8.kubernetes.api.model.LabelSelector;

public class ResilientService2 {

  private String namespace;
  private String strategy;
  private LabelSelector selector;
  private FaultTemplate faultTemplate;
}
