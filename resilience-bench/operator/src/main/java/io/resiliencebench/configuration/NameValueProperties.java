package io.resiliencebench.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a list of name-value pairs, with serialization and deserialization capabilities.
 */
public class NameValueProperties extends ArrayList<NameValueProperties.Attribute> {

  public NameValueProperties() {
    super();
  }

  public NameValueProperties(Attribute... attributes) {
    super(Arrays.asList(attributes));
  }

  @Override
  public String toString() {
    String serialized = this.stream()
            .map(Attribute::toString)
            .collect(Collectors.joining("."));
    return serialized.isEmpty() ? "none" : serialized;
  }

  /**
   * Represents a single name-value pair attribute.
   */
  public static class Attribute {
    @JsonIgnore
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String name;
    private final JsonNode value;

    public Attribute() {
      this.name = "";
      this.value = null;
    }

    public Attribute(String name, JsonNode value) {
      this.name = name;
      this.value = value;
    }

    public Attribute(String name, Object value) {
      this(name, MAPPER.valueToTree(value));
    }

    public String getName() {
      return name;
    }

    public JsonNode getValue() {
      return value;
    }

    /**
     * Converts the JsonNode value to a corresponding Java object.
     *
     * @return the Java object representation of the value
     */
    @JsonIgnore
    public Optional<Object> getValueAsObject() {
      if (value == null) {
        return Optional.empty();
      } else if (value.isTextual()) {
        return Optional.of(value.asText());
      } else if (value.isNumber()) {
        return Optional.of(getNumericValue(value));
      } else if (value.isBoolean()) {
        return Optional.of(value.asBoolean());
      } else if (value.isArray()) {
        return Optional.of(getListValue(value));
      } else {
        return Optional.of(value);
      }
    }

    private Object getNumericValue(JsonNode value) {
      if (value.isDouble() || value.isFloatingPointNumber()) {
        return value.doubleValue();
      } else if (value.isLong()) {
        return value.longValue();
      } else {
        return value.intValue();
      }
    }

    private List<Object> getListValue(JsonNode value) {
      List<Object> list = new ArrayList<>();
      value.elements().forEachRemaining(list::add);
      return list;
    }

    @Override
    public String toString() {
      return String.format("%s-%s", name, getValueAsObject().orElse("null"));
    }
  }
}
