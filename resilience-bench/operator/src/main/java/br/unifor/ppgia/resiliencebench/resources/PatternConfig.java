package br.unifor.ppgia.resiliencebench.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class PatternConfig extends ArrayList<PatternConfig.Attribute> {
  public PatternConfig() {
  }

  public PatternConfig(Attribute... params) {
    this.addAll(Arrays.asList(params));
  }

  @Override
  public String toString() {
    var serialized = this.stream()
            .map(Attribute::toString)
            .collect(joining("."));
    if (serialized.isEmpty()) {
      serialized = "none";
    }
    return serialized;
  }

  public static class Attribute {
    @JsonIgnore
    private static final ObjectMapper mapper = new ObjectMapper();

    private String name;
    private JsonNode value;

    public Attribute() {
    }

    public Attribute(String name, JsonNode value) {
      this.name = name;
      this.value = value;
    }

    public Attribute(String name, Object value) {
      this(name, mapper.valueToTree(value));
    }

    public String getName() {
      return name;
    }

    public JsonNode getValue() {
      return value;
    }

    @JsonIgnore
    public Object getValueAsObject() {
      if (getValue().isTextual()) {
        return getValue().asText();
      } else if (getValue().isNumber()) {
        if (getValue().isDouble() || getValue().isFloatingPointNumber()) {
          return getValue().doubleValue();
        } else if (getValue().isLong()) {
          return getValue().longValue();
        } else {
          return getValue().intValue();
        }
      } else if (getValue().isBoolean()) {
        return getValue().asBoolean();
      } else if (getValue().isArray()) {
        var list = new ArrayList<>();
        getValue().elements().forEachRemaining(list::add);
        return list;
      } else {
        return getValue();
      }
    }

    @Override
    public String toString() {
      return getName() + "-" + getValueAsObject();
    }
  }
}
