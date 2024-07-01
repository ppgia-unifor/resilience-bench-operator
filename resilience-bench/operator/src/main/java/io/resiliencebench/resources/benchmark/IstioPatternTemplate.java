package io.resiliencebench.resources.benchmark;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.resiliencebench.resources.NameValueProperties;

public class IstioPatternTemplate {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private NameValueProperties retry;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private NameValueProperties timeout;
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private NameValueProperties circuitBreaker;

  public IstioPatternTemplate() {
  }

  public IstioPatternTemplate(NameValueProperties retry, NameValueProperties timeout, NameValueProperties circuitBreaker) {
    this.retry = retry;
    this.timeout = timeout;
    this.circuitBreaker = circuitBreaker;
  }

  public NameValueProperties getRetry() {
    return retry;
  }

  public NameValueProperties getTimeout() {
    return timeout;
  }

  public NameValueProperties getCircuitBreaker() {
    return circuitBreaker;
  }
}
