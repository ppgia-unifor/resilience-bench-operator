package br.unifor.ppgia.resiliencebench.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PatternConfig {

  @JsonIgnore
  private static final ObjectMapper mapper = new ObjectMapper();

  public PatternConfig() {
  }

  public PatternConfig(String name, JsonNode value) {
    this.name = name;
    this.value = value;
  }

  public PatternConfig(String name, Object value) {
    this.name = name;
    this.value = mapper.valueToTree(value);
  }

  private String name;
  private JsonNode value;

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
      } else {
        return getValue().longValue();
      }
    } else if (getValue().isBoolean()) {
      return getValue().asBoolean();
    } else {
      // For other types, you can add more conditions as needed.
      return getValue(); // Fallback, returns the JsonNode itself.
    }
  }
}
