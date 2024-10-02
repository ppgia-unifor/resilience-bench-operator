package io.resiliencebench.support;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonNodeObjects {

  public JsonNodeObjects() {
    throw new IllegalStateException("Utility class");
  }

  public static Object toObject(JsonNode jsonNode) {
    if (jsonNode.isObject()) {
      Map<String, Object> map = new HashMap<>();
      jsonNode.fields().forEachRemaining(entry -> map.put(entry.getKey(), toObject(entry.getValue())));
      return map;
    } else if (jsonNode.isArray()) {
      List<Object> list = new ArrayList<>();
      jsonNode.elements().forEachRemaining(element -> list.add(toObject(element)));
      return list;
    } else if (jsonNode.isTextual()) {
      return jsonNode.asText();
    } else if (jsonNode.isBoolean()) {
      return jsonNode.asBoolean();
    } else if (jsonNode.isNumber()) {
      if (jsonNode.isDouble() || jsonNode.isFloatingPointNumber()) {
        return jsonNode.doubleValue();
      } else if (jsonNode.isLong()) {
        return jsonNode.longValue();
      } else {
        return jsonNode.intValue();
      }
    } else if (jsonNode.isNull()) {
      return null;
    }

    return jsonNode;
  }
}
