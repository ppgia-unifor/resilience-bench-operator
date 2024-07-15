package io.resiliencebench.resources.benchmark;

import io.fabric8.generator.annotation.Nullable;

public class PatternTemplate {

  @Nullable
  private IstioPatternTemplate istio;

  public PatternTemplate() {
  }

  public PatternTemplate(IstioPatternTemplate istio) {
    this.istio = istio;
  }

  public IstioPatternTemplate getIstio() {
    return istio;
  }
}
