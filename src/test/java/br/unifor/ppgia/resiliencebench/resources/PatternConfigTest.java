package br.unifor.ppgia.resiliencebench.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PatternConfigTest {
  private static final ObjectMapper mapper = new ObjectMapper();
  private PatternConfig patternConfig;
  private String testName;
  private JsonNode testValue;

  @BeforeEach
  void setUp() {
    testName = "testName";
    testValue = TextNode.valueOf("testValue");
    patternConfig = new PatternConfig(testName, testValue);
  }

  @Test
  void testDefaultConstructor() {
    PatternConfig defaultConfig = new PatternConfig();
    assertNull(defaultConfig.getName());
    assertNull(defaultConfig.getValue());
  }

  @Test
  void testConstructorWithNameAndJsonNode() {
    assertEquals(testName, patternConfig.getName());
    assertEquals(testValue, patternConfig.getValue());
  }

  @Test
  void testConstructorWithNameAndObject() {
    String testObject = "testObject";
    PatternConfig objectConfig = new PatternConfig(testName, testObject);
    assertEquals(testName, objectConfig.getName());
    assertEquals(testObject, objectConfig.getValue().asText());
  }

  @Test
  void testGetName() {
    assertEquals(testName, patternConfig.getName());
  }

  @Test
  void testGetValue() {
    assertEquals(testValue, patternConfig.getValue());
  }

  @Test
  void testGetValueAsObjectTextual() {
    assertEquals(testValue.asText(), patternConfig.getValueAsObject());
  }

  @Test
  void testGetValueAsObjectNumeric() {
    PatternConfig numericConfig = new PatternConfig(testName, 123);
    assertEquals(123L, numericConfig.getValueAsObject());
  }

  @Test
  void testGetValueAsObjectBoolean() {
    PatternConfig booleanConfig = new PatternConfig(testName, true);
    assertEquals(true, booleanConfig.getValueAsObject());
  }

  @Test
  void testGetValueAsObjectOtherType() {
    JsonNode jsonNode = mapper.createObjectNode().put("key", "value");
    PatternConfig nodeConfig = new PatternConfig(testName, jsonNode);
    assertEquals(jsonNode, nodeConfig.getValueAsObject());
  }
}
