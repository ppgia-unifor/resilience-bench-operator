package br.unifor.ppgia.resiliencebench.resources.modeling.service;

import io.fabric8.kubernetes.api.model.LabelSelector;

public class ResilientServiceSpec {

  private LabelSelector selector;

  public LabelSelector getSelector() {
    return selector;
  }

  public void setSelector(LabelSelector selector) {
    this.selector = selector;
  }
}
