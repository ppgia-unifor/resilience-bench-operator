package br.unifor.ppgia.resiliencebench.support;

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

    public static List<Map<String, Object>> expandConfigTemplate(PatternConfig patternConfigs) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(new HashMap<>());

        for (var config : patternConfigs) {
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
            return jsonNode;
        }
    }
}