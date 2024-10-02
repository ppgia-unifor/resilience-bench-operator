package io.resiliencebench.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonNodeObjectsTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void testConvertJsonObject() throws Exception {
    String json = "{\"key1\": \"value1\", \"key2\": 123, \"key3\": true}";
    JsonNode jsonNode = objectMapper.readTree(json);

    Object result = JsonNodeObjects.toObject(jsonNode);

    assertInstanceOf(Map.class, result);
    Map<String, Object> resultMap = (Map<String, Object>) result;

    assertEquals("value1", resultMap.get("key1"));
    assertEquals(123, resultMap.get("key2"));
    assertEquals(true, resultMap.get("key3"));
  }

  @Test
  void testConvertJsonArray() throws Exception {
    String json = "[\"item1\", 42, false]";
    JsonNode jsonNode = objectMapper.readTree(json);

    Object result = JsonNodeObjects.toObject(jsonNode);

    assertInstanceOf(List.class, result);
    List<Object> resultList = (List<Object>) result;

    assertEquals("item1", resultList.get(0));
    assertEquals(42, resultList.get(1));
    assertEquals(false, resultList.get(2));
  }

  @Test
  void testConvertJsonString() throws Exception {
    JsonNode jsonNode = objectMapper.readTree("\"Hello, World!\"");

    Object result = JsonNodeObjects.toObject(jsonNode);

    assertInstanceOf(String.class, result);
    assertEquals("Hello, World!", result);
  }

  @Test
  void testConvertJsonBoolean() throws Exception {
    JsonNode jsonNode = objectMapper.readTree("true");

    Object result = JsonNodeObjects.toObject(jsonNode);

    assertInstanceOf(Boolean.class, result);
    assertEquals(true, result);
  }

  @Test
  void testConvertJsonNumber() throws Exception {
    // Test integer
    JsonNode jsonNodeInt = objectMapper.readTree("123");

    Object resultInt = JsonNodeObjects.toObject(jsonNodeInt);
    assertInstanceOf(Integer.class, resultInt);
    assertEquals(123, resultInt);

    // Test double
    JsonNode jsonNodeDouble = objectMapper.readTree("123.45");

    Object resultDouble = JsonNodeObjects.toObject(jsonNodeDouble);
    assertInstanceOf(Double.class, resultDouble);
    assertEquals(123.45, resultDouble);
  }

  @Test
  void testConvertJsonNull() throws Exception {
    JsonNode jsonNode = objectMapper.readTree("null");

    Object result = JsonNodeObjects.toObject(jsonNode);

    assertNull(result);
  }

  @Test
  void testComplexJsonObject() throws Exception {
    String json = "{\"key1\": \"value1\", \"key2\": [1, 2, 3], \"key3\": {\"subKey\": \"subValue\"}}";
    JsonNode jsonNode = objectMapper.readTree(json);

    Object result = JsonNodeObjects.toObject(jsonNode);

    assertInstanceOf(Map.class, result);
    Map<String, Object> resultMap = (Map<String, Object>) result;

    assertEquals("value1", resultMap.get("key1"));

    // Test nested array
    List<Object> nestedList = (List<Object>) resultMap.get("key2");
    assertEquals(3, nestedList.size());
    assertEquals(1, nestedList.get(0));
    assertEquals(2, nestedList.get(1));
    assertEquals(3, nestedList.get(2));

    // Test nested object
    Map<String, Object> nestedMap = (Map<String, Object>) resultMap.get("key3");
    assertEquals("subValue", nestedMap.get("subKey"));
  }
}
