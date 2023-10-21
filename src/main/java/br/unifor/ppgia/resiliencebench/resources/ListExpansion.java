package br.unifor.ppgia.resiliencebench.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListExpansion {
    public static List<Map<String, Object>> generateConfig(Map<String, Object> configTemplate, List<Map.Entry<String, List<Object>>> keyExpansionList) {
        List<Map<String, Object>> configList = new ArrayList<>();

        if (!keyExpansionList.isEmpty()) {
            Map.Entry<String, List<Object>> entry = keyExpansionList.get(0);
            String key = entry.getKey();
            List<Object> valList = entry.getValue();

            if (configTemplate.containsKey(key)) {
                // generate config instances for each key value
                for (Object val : valList) {
                    Map<String, Object> config = new HashMap<>(configTemplate);
                    config.put(key, val);

                    // check whether there are still keys to expand
                    if (keyExpansionList.size() > 1) {
                        // recursively generate new config instances using current (expanded) config instance as template
                        configList.addAll(generateConfig(config, keyExpansionList.subList(1, keyExpansionList.size())));
                    } else {
                        // add fully expanded config instance to config list
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
            // generate all possible config instance combinations from
            // the given config template and key expansion list
            return generateConfig(configTemplate, keyExpansionList);
        } else {
            // no template expansion needed
            List<Map<String, Object>> configList = new ArrayList<>();
            configList.add(configTemplate);
            return configList;
        }
    }
}