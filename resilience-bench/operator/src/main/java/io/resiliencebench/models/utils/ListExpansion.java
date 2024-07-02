package io.resiliencebench.models.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.resiliencebench.configuration.NameValueProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for expanding configuration templates.
 */
public final class ListExpansion {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ListExpansion() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Expands a configuration template based on the provided NameValueProperties.
     *
     * @param patternConfigs the pattern configurations to expand
     * @return a list of maps representing the expanded configurations
     */
    public static List<Map<String, Object>> expandConfigTemplate(NameValueProperties patternConfigs) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(new HashMap<>());

        for (var config : patternConfigs) {
            String key = config.getName();
            JsonNode value = config.getValue();

            if (value.isArray()) {
                resultList = multiplyList(resultList, key, (ArrayNode) value);
            } else {
                resultList.forEach(map -> map.put(key, jsonNodeToObject(value)));
            }
        }

        return resultList;
    }

    /**
     * Multiplies the current list of maps by adding new entries for each element in the array.
     *
     * @param currentList the current list of maps
     * @param key         the key to add to the maps
     * @param valueArray  the array of values to add
     * @return a new list of maps with the expanded entries
     */
    private static List<Map<String, Object>> multiplyList(List<Map<String, Object>> currentList, String key, ArrayNode valueArray) {
        List<Map<String, Object>> newList = new ArrayList<>(currentList.size() * valueArray.size());

        for (Map<String, Object> existingMap : currentList) {
            for (JsonNode arrayItem : valueArray) {
                Map<String, Object> newMap = new HashMap<>(existingMap);
                newMap.put(key, jsonNodeToObject(arrayItem));
                newList.add(newMap);
            }
        }

        return newList;
    }

    /**
     * Converts a JsonNode to its corresponding Java object representation.
     *
     * @param jsonNode the JsonNode to convert
     * @return the corresponding Java object
     */
    private static Object jsonNodeToObject(JsonNode jsonNode) {
        if (jsonNode.isTextual()) {
            return jsonNode.asText();
        } else if (jsonNode.isNumber()) {
            return jsonNode.numberValue();
        } else if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        } else if (jsonNode.isArray()) {
            List<Object> list = new ArrayList<>(jsonNode.size());
            jsonNode.forEach(item -> list.add(jsonNodeToObject(item)));
            return list;
        } else if (jsonNode.isObject()) {
            Map<String, Object> map = new HashMap<>();
            jsonNode.fields().forEachRemaining(entry -> map.put(entry.getKey(), jsonNodeToObject(entry.getValue())));
            return map;
        } else {
            return null; // or throw an exception if appropriate
        }
    }
}
