package br.unifor.ppgia.resiliencebench.resources;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListExpansionTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void should_expand_template() {
    Map<String, Object> configTemplate = new HashMap<>();
    configTemplate.put("slowCallRateThreshold", "100");
    configTemplate.put("slowCallDurationThreshold", "1000");
    configTemplate.put("waitDurationInOpenState", List.of("50", "100", "200"));

    var expandedConfigs = ListExpansion.expandConfigTemplate(configTemplate);

    Assertions.assertEquals(3, expandedConfigs.size());
    Assertions.assertEquals(expandedConfigs.get(0).get("waitDurationInOpenState"), "50");
    Assertions.assertEquals(expandedConfigs.get(1).get("waitDurationInOpenState"), "100");
    Assertions.assertEquals(expandedConfigs.get(2).get("waitDurationInOpenState"), "200");
  }

  @Test
  public void should_expand_multiple_templates() {
    Map<String, Object> configTemplate = new HashMap<>();
    configTemplate.put("slowCallRateThreshold", "100");
    configTemplate.put("slowCallDurationThreshold", List.of("1000", "2000"));
    configTemplate.put("waitDurationInOpenState", List.of("50", "100", "200"));
    var expandedConfigs = ListExpansion.expandConfigTemplate(configTemplate);

    Assertions.assertEquals(6, expandedConfigs.size());
    Assertions.assertEquals(expandedConfigs.get(0).get("waitDurationInOpenState"), "50");
    Assertions.assertEquals(expandedConfigs.get(0).get("slowCallDurationThreshold"), "1000");
    Assertions.assertEquals(expandedConfigs.get(1).get("waitDurationInOpenState"), "100");
    Assertions.assertEquals(expandedConfigs.get(1).get("slowCallDurationThreshold"), "1000");
  }

  @Test
  public void should_expand_template_as_json() {
    Map<String, JsonNode> configTemplate = new HashMap<>();
    configTemplate.put("slowCallRateThreshold", objectMapper.valueToTree(100));
    configTemplate.put("slowCallDurationThreshold", objectMapper.valueToTree(1000));
    configTemplate.put("waitDurationInOpenState", objectMapper.valueToTree(List.of(50, 100, 200)));

    var expandedConfigs = ListExpansion.expandConfigTemplateJson(configTemplate);

    Assertions.assertEquals(3, expandedConfigs.size());
    Assertions.assertEquals(expandedConfigs.get(0).get("waitDurationInOpenState"), 50);
    Assertions.assertEquals(expandedConfigs.get(1).get("waitDurationInOpenState"), 100);
    Assertions.assertEquals(expandedConfigs.get(2).get("waitDurationInOpenState"), 200);
  }

  @Test
  public void should_expand_multiple_templates_as_json() {
    Map<String, JsonNode> configTemplate = new HashMap<>();
    configTemplate.put("slowCallRateThreshold", objectMapper.valueToTree(100));
    configTemplate.put("slowCallDurationThreshold", objectMapper.valueToTree(List.of(1000, 2000)));
    configTemplate.put("waitDurationInOpenState", objectMapper.valueToTree(List.of(50, 100, 200)));
    var expandedConfigs = ListExpansion.expandConfigTemplateJson(configTemplate);

    Assertions.assertEquals(6, expandedConfigs.size());
    Assertions.assertEquals(expandedConfigs.get(0).get("waitDurationInOpenState"), 50);
    Assertions.assertEquals(expandedConfigs.get(0).get("slowCallDurationThreshold"), 1000);
    Assertions.assertEquals(expandedConfigs.get(1).get("waitDurationInOpenState"), 100);
    Assertions.assertEquals(expandedConfigs.get(1).get("slowCallDurationThreshold"), 1000);
  }

  @Test
  public void should_expand_template_as_patternConfig() {
    PatternConfig configTemplate = new PatternConfig();

    configTemplate.add(new PatternConfig.Attribute("slowCallRateThreshold", objectMapper.valueToTree(100)));
    configTemplate.add(new PatternConfig.Attribute("slowCallDurationThreshold", objectMapper.valueToTree(1000)));
    configTemplate.add(new PatternConfig.Attribute("waitDurationInOpenState", objectMapper.valueToTree(List.of(50, 100, 200))));

    var expandedConfigs = ListExpansion.expandConfigTemplate(configTemplate);

    Assertions.assertEquals(3, expandedConfigs.size());
    Assertions.assertEquals(expandedConfigs.get(0).get("waitDurationInOpenState"), 50);
    Assertions.assertEquals(expandedConfigs.get(1).get("waitDurationInOpenState"), 100);
    Assertions.assertEquals(expandedConfigs.get(2).get("waitDurationInOpenState"), 200);
  }

  @Test
  public void should_expand_simple_template_as_patternConfig() {
    PatternConfig configTemplate = new PatternConfig();

    configTemplate.add(new PatternConfig.Attribute("slowCallRateThreshold",100));
    configTemplate.add(new PatternConfig.Attribute("slowCallDurationThreshold",1000));
    configTemplate.add(new PatternConfig.Attribute("waitDurationInOpenState", 200));

    var expandedConfigs = ListExpansion.expandConfigTemplate(configTemplate);

    Assertions.assertEquals(1, expandedConfigs.size());
    Assertions.assertEquals(expandedConfigs.get(0).get("slowCallRateThreshold"), 100);
    Assertions.assertEquals(expandedConfigs.get(0).get("slowCallDurationThreshold"), 1000);
    Assertions.assertEquals(expandedConfigs.get(0).get("waitDurationInOpenState"), 200);
  }

  @Test
  public void should_expand_multiple_templates_as_patternConfig() {
    PatternConfig configTemplate = new PatternConfig();
    configTemplate.add(new PatternConfig.Attribute("slowCallRateThreshold", 100));
    configTemplate.add(new PatternConfig.Attribute("slowCallDurationThreshold", List.of(1000, 2000)));
    configTemplate.add(new PatternConfig.Attribute("waitDurationInOpenState", List.of(50, 100, 200)));
    var expandedConfigs = ListExpansion.expandConfigTemplate(configTemplate);

    Assertions.assertEquals(6, expandedConfigs.size());
    Assertions.assertEquals(expandedConfigs.get(0).get("waitDurationInOpenState"), 50);
    Assertions.assertEquals(expandedConfigs.get(0).get("slowCallDurationThreshold"), 1000);
    Assertions.assertEquals(expandedConfigs.get(1).get("waitDurationInOpenState"), 100);
    Assertions.assertEquals(expandedConfigs.get(1).get("slowCallDurationThreshold"), 1000);
  }
}

