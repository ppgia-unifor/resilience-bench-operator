package io.resiliencebench.models.workload.builder.script;

import io.resiliencebench.models.workload.configuration.script.ScriptConfig;
import io.resiliencebench.support.ConfigMapReference;

/**
 * Builder class for creating instances of ScriptConfig.
 */
public class ScriptConfigBuilder {
	private ConfigMapReference configMap;

	public ScriptConfigBuilder withConfigMap(ConfigMapReference configMap) {
		this.configMap = configMap;
		return this;
	}

	public ScriptConfig build() {
		return new ScriptConfig(configMap);
	}
}