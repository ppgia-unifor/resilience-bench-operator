package io.resiliencebench.resources.scenario;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonObject;

import java.util.Map;

import static io.resiliencebench.resources.Maps.toJsonMap;
import static io.resiliencebench.resources.Maps.toObjectMap;

public class IstioPattern {

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> retry;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> timeout;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> circuitBreaker;

  public IstioPattern() { }

  public IstioPattern(Map<String, Object> retry, Map<String, Object> timeout, Map<String, Object> circuitBreaker) {
    this.retry = toJsonMap(retry);
    this.timeout = toJsonMap(timeout);
    this.circuitBreaker = toJsonMap(circuitBreaker);
  }

  public Map<String, Object> getRetry() {
    return toObjectMap(retry);
  }

  public Map<String, Object> getTimeout() {
    return toObjectMap(timeout);
  }

  public Map<String, Object> getCircuitBreaker() {
    return toObjectMap(circuitBreaker);
  }

  public JsonObject toJson() {
    return JsonObject.mapFrom(this);
  }
}
