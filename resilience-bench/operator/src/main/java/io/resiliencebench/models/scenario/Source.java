package io.resiliencebench.models.scenario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a source in the resilience benchmark.
 */
public class Source {

  @JsonIgnore
  private static final ObjectMapper mapper = new ObjectMapper();

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Map<String, JsonNode> patternConfig;

  private String serviceName;

  /**
   * Default constructor for Source.
   */
  public Source() {
    // Default constructor for serialization/deserialization
  }

  /**
   * Constructs a Source with the specified service name and pattern configuration.
   *
   * @param serviceName   the name of the service
   * @param patternConfig the pattern configuration map
   */
  public Source(String serviceName, Map<String, Object> patternConfig) {
    this.serviceName = serviceName;
    setPatternConfig(patternConfig);
  }

  /**
   * Returns the name of the service.
   *
   * @return the service name
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   * Sets the name of the service.
   *
   * @param serviceName the service name to set
   */
  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  /**
   * Returns a copy of the expanded pattern configuration map.
   *
   * @return the pattern configuration map
   */
  public Map<String, Object> getPatternConfig() {
    if (patternConfig == null) {
      return Collections.emptyMap();
    }
    return patternConfig.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> Optional.ofNullable(toObject(entry.getValue())).orElse(""),
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));
  }

  /**
   * Sets the pattern configuration map.
   *
   * @param patternConfig the pattern configuration map to set
   */
  public void setPatternConfig(Map<String, Object> patternConfig) {
    this.patternConfig = patternConfig.entrySet().stream()
            .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> mapper.valueToTree(entry.getValue()),
                    (e1, e2) -> e1,
                    LinkedHashMap::new
            ));
  }

  /**
   * Converts a JsonNode to a Java object.
   *
   * @param jsonNode the JsonNode to convert
   * @return the corresponding Java object
   */
  private static Object toObject(JsonNode jsonNode) {
    if (jsonNode.isArray()) {
      List<Object> list = new ArrayList<>();
      jsonNode.forEach(element -> list.add(toObject(element)));
      return list;
    } else if (jsonNode.isObject()) {
      Map<String, Object> map = new LinkedHashMap<>();
      jsonNode.fields().forEachRemaining(entry -> map.put(entry.getKey(), toObject(entry.getValue())));
      return map;
    } else if (jsonNode.isTextual()) {
      return jsonNode.textValue();
    } else if (jsonNode.isBoolean()) {
      return jsonNode.booleanValue();
    } else if (jsonNode.isNumber()) {
      return jsonNode.numberValue();
    } else if (jsonNode.isNull()) {
      return null;
    }

    return jsonNode;
  }

  /**
   * Returns a string representation of the Source.
   *
   * @return a string representation of the Source
   */
  @Override
  public String toString() {
    return "Source{" +
            "serviceName='" + serviceName + '\'' +
            ", patternConfig=" + patternConfig +
            '}';
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare
   * @return true if this object is the same as the obj argument; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Source source = (Source) o;
    return Objects.equals(serviceName, source.serviceName) &&
            Objects.equals(patternConfig, source.patternConfig);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(serviceName, patternConfig);
  }
}
