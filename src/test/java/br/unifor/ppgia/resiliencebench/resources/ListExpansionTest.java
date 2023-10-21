package br.unifor.ppgia.resiliencebench.resources;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListExpansionTest {
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
}

