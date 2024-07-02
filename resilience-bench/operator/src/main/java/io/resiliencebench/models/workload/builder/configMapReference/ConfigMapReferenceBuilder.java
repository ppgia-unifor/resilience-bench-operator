package io.resiliencebench.models.workload.builder.configMapReference;

import io.resiliencebench.support.ConfigMapReference;

/**
 * Builder class for ConfigMapReference.
 */
public class ConfigMapReferenceBuilder {

	private String name;
	private String file;

	/**
	 * Sets the name of the ConfigMap.
	 *
	 * @param name the name of the ConfigMap
	 * @return the Builder instance
	 */
	public ConfigMapReferenceBuilder withName(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("ConfigMap name cannot be null or empty");
		}
		this.name = name;
		return this;
	}

	/**
	 * Sets the file within the ConfigMap.
	 *
	 * @param file the file within the ConfigMap
	 * @return the Builder instance
	 */
	public ConfigMapReferenceBuilder withFile(String file) {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("ConfigMap file cannot be null or empty");
		}
		this.file = file;
		return this;
	}

	/**
	 * Builds and returns a ConfigMapReference instance.
	 *
	 * @return the ConfigMapReference instance
	 */
	public ConfigMapReference build() {
		return new ConfigMapReference(name, file);
	}
}
