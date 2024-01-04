package br.unifor.ppgia.resiliencebench.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ListExpansion {
    public ListExpansion() {
        throw new IllegalStateException("Utility class");
    }

    public static List<Map<String, Object>> expandConfigTemplate(Map<String, Object> configTemplate) {
        List<Map.Entry<String, List<Object>>> keyExpansionList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : configTemplate.entrySet()) {
            if (entry.getValue() instanceof List) {
                List<Object> valList = (List<Object>) entry.getValue();
                keyExpansionList.add(Map.entry(entry.getKey(), valList));
            }
        }

        if (!keyExpansionList.isEmpty()) {
            return generateConfig(configTemplate, keyExpansionList);
        } else {
            List<Map<String, Object>> configList = new ArrayList<>();
            configList.add(configTemplate);
            return configList;
        }
    }

    public static List<Map<String, Object>> expandConfigTemplateJson(Map<String, JsonNode> inputMap) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(new HashMap<>());

        for (Map.Entry<String, JsonNode> entry : inputMap.entrySet()) {
            JsonNode value = entry.getValue();

            if (value.isArray()) {
                ArrayNode arrayNode = (ArrayNode) value;
                resultList = multiplyList(resultList, entry.getKey(), arrayNode);
            } else {
                for (Map<String, Object> map : resultList) {
                    map.put(entry.getKey(), jsonNodeToObject(value));
                }
            }
        }

        return resultList;
    }

    public static List<Map<String, Object>> expandConfigTemplate(List<PatternConfig> patternConfigs) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(new HashMap<>());

        for (PatternConfig config : patternConfigs) {
            String key = config.getName();
            JsonNode value = config.getValue();

            if (value.isArray()) {
                ArrayNode arrayNode = (ArrayNode) value;
                resultList = multiplyList(resultList, key, arrayNode);
            } else {
                for (Map<String, Object> map : resultList) {
                    map.put(key, jsonNodeToObject(value));
                }
            }
        }

        return resultList;
    }

    private static List<Map<String, Object>> generateConfig(Map<String, Object> configTemplate, List<Map.Entry<String, List<Object>>> keyExpansionList) {
        List<Map<String, Object>> configList = new ArrayList<>();

        if (!keyExpansionList.isEmpty()) {
            Map.Entry<String, List<Object>> entry = keyExpansionList.get(0);
            String key = entry.getKey();
            List<Object> valList = entry.getValue();

            if (configTemplate.containsKey(key)) {
                for (Object val : valList) {
                    Map<String, Object> config = new HashMap<>(configTemplate);
                    config.put(key, val);
                    if (keyExpansionList.size() > 1) {
                        configList.addAll(generateConfig(config, keyExpansionList.subList(1, keyExpansionList.size())));
                    } else {
                        configList.add(config);
                    }
                }
            }
        }

        return configList;
    }

    private static List<Map<String, Object>> multiplyList(List<Map<String, Object>> currentList, String key, ArrayNode valueArray) {
        List<Map<String, Object>> newList = new ArrayList<>();

        for (Map<String, Object> existingMap : currentList) {
            for (JsonNode arrayItem : valueArray) {
                Map<String, Object> newMap = new HashMap<>(existingMap);
                newMap.put(key, jsonNodeToObject(arrayItem));
                newList.add(newMap);
            }
        }

        return newList;
    }

    private static Object jsonNodeToObject(JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            return jsonNode.asText();
        } else if (jsonNode.isNumber()) {
            if (jsonNode.isDouble() || jsonNode.isFloatingPointNumber()) {
                return jsonNode.doubleValue();
            } else if (jsonNode.isLong()) {
                return jsonNode.longValue();
            } else {
                return jsonNode.intValue();
            }
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        } else {
            // For other types, you can add more conditions as needed.
            return jsonNode; // Fallback, returns the JsonNode itself.
        }
    }
}