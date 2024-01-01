package br.unifor.ppgia.resiliencebench.resources.scenario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioSpecTest {

  private final static ObjectMapper mapper = new ObjectMapper();

  @Test
  void testPatternConfigWithString() {
    ScenarioSpec spec = new ScenarioSpec();
    String key = "testKey";
    String value = "testValue";
    JsonNode jsonNode = mapper.valueToTree(value);

    spec.setPatternConfig(key, jsonNode);

    assertEquals(value, spec.getPatternConfig().get(key).textValue());
  }

  @Test
  void testPatternConfigWithBoolean() {
    ScenarioSpec spec = new ScenarioSpec();
    String key = "testBoolean";
    boolean value = true;
    JsonNode jsonNode = mapper.valueToTree(value);

    spec.setPatternConfig(key, jsonNode);

    assertEquals(value, spec.getPatternConfig().get(key).booleanValue());
  }

  @Test
  void testPatternConfigWithNumber() {
    ScenarioSpec spec = new ScenarioSpec();
    String key = "testNumber";
    int value = 123;
    JsonNode jsonNode = mapper.valueToTree(value);

    spec.setPatternConfig(key, jsonNode);

    assertEquals(value, spec.getPatternConfig().get(key).intValue());
  }

  @Test
  void testPatternConfigWithArray() {
    ScenarioSpec spec = new ScenarioSpec();
    String key = "testArray";
    List<Integer> value = List.of(1, 2, 3);
    JsonNode jsonNode = mapper.valueToTree(value);

    spec.setPatternConfig(key, jsonNode);

    assertEquals(1, spec.getPatternConfig().get(key).get(0).intValue());
    assertEquals(2, spec.getPatternConfig().get(key).get(1).intValue());
    assertEquals(3, spec.getPatternConfig().get(key).get(2).intValue());
  }

  @Test
  void testPatternConfigWithObject() {
    ScenarioSpec spec = new ScenarioSpec();
    String key = "testObject";
    Map<String, Object> value = new HashMap<>();
    value.put("nestedKey", "nestedValue");
    JsonNode jsonNode = mapper.valueToTree(value);

    spec.setPatternConfig(key, jsonNode);

    assertEquals("nestedValue", spec.getPatternConfig().get(key).get("nestedKey").textValue());
  }

  @Test
  void testPatternConfigWithNull() {
    ScenarioSpec spec = new ScenarioSpec();
    String key = "testNull";

    spec.setPatternConfig(key, null);

    assertNull(spec.getPatternConfig().get(key));
  }

  @Test
  void testPatternConfigWithStringUsingConstructor() {
    Map<String, Object> patternConfig = new HashMap<>();
    String key = "testKey";
    String value = "testValue";
    patternConfig.put(key, value);

    ScenarioSpec spec = new ScenarioSpec(null, null, patternConfig, null, null);

    assertEquals(value, spec.getPatternConfig().get(key).textValue());
  }

  @Test
  void testPatternConfigWithBooleanUsingConstructor() {
    Map<String, Object> patternConfig = new HashMap<>();
    String key = "testBoolean";
    boolean value = true;
    patternConfig.put(key, value);

    ScenarioSpec spec = new ScenarioSpec(null, null, patternConfig, null, null);

    assertEquals(value, spec.getPatternConfig().get(key).booleanValue());
  }

  @Test
  void testPatternConfigWithNumberUsingConstructor() {
    Map<String, Object> patternConfig = new HashMap<>();
    String key = "testNumber";
    int value = 123;
    patternConfig.put(key, value);

    ScenarioSpec spec = new ScenarioSpec(null, null, patternConfig, null, null);

    assertEquals(value, spec.getPatternConfig().get(key).intValue());
  }

  @Test
  void testPatternConfigWithArrayUsingConstructor() {
    Map<String, Object> patternConfig = new HashMap<>();
    String key = "testArray";
    List<Integer> value = List.of(1, 2, 3);
    patternConfig.put(key, value);

    ScenarioSpec spec = new ScenarioSpec(null, null, patternConfig, null, null);

    // Assuming JsonNode converts lists to arrays
    JsonNode arrayNode = spec.getPatternConfig().get(key);
    Assertions.assertNotNull(arrayNode);
    assertTrue(arrayNode.isArray());
    assertEquals(3, arrayNode.size());
    for (int i = 0; i < arrayNode.size(); i++) {
      assertEquals(value.get(i).intValue(), arrayNode.get(i).intValue());
    }
  }

  @Test
  void testPatternConfigWithObjectUsingConstructor() {
    Map<String, Object> patternConfig = new HashMap<>();
    String key = "testObject";
    Map<String, Object> nestedMap = new HashMap<>();
    nestedMap.put("nestedKey", "nestedValue");
    patternConfig.put(key, nestedMap);

    ScenarioSpec spec = new ScenarioSpec(null, null, patternConfig, null, null);

    assertEquals("nestedValue", spec.getPatternConfig().get(key).get("nestedKey").textValue());
  }

  @Test
  void testPatternConfigWithNullUsingConstructor() {
    Map<String, Object> patternConfig = new HashMap<>();
    String key = "testNull";
    patternConfig.put(key, null);

    ScenarioSpec spec = new ScenarioSpec(null, null, patternConfig, null, null);

    assertEquals(spec.getPatternConfig().get(key), NullNode.getInstance());
  }
}