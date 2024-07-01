package io.resiliencebench.resources.scenario;

import io.resiliencebench.resources.fault.AbortFault;
import io.resiliencebench.resources.fault.DelayFault;
import io.resiliencebench.resources.fault.FaultTemplate;
import io.vertx.core.json.JsonObject;

public class Fault extends FaultTemplate<Integer> {

  public Fault() {
    super();
  }

  public Fault(Integer percentage, AbortFault abort) {
    super(percentage, abort);
  }

  public Fault(Integer percentage, DelayFault delay) {
    super(percentage, delay);
  }

  public static Fault create(Integer percentage, DelayFault delay, AbortFault abort) {
    if (delay != null) {
      return new Fault(percentage, delay);
    } else if (abort != null) {
      return new Fault(percentage, abort);
    } else {
      return null;
    }
  }

  public JsonObject toJson() {
    return JsonObject.of(
            "percentage", getPercentage(),
            "delay", getDelay() == null ? null : JsonObject.of("duration", getDelay().duration()),
            "abort", getAbort() == null ? null : JsonObject.of("code", getAbort().httpStatus())
    );
  }
}
