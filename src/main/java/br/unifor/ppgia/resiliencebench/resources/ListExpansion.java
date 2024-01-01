package br.unifor.ppgia.resiliencebench.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ListExpansion {
    public ListExpansion() {
        throw new IllegalStateException("Utility class");
    }
    public static List<Map<String, Object>> generateConfig(Map<String, Object> configTemplate, List<Map.Entry<String, List<Object>>> keyExpansionList) {
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
}