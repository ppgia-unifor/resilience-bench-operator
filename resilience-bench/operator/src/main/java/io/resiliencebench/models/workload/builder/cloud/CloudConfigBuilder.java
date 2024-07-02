package io.resiliencebench.models.workload.builder.cloud;

import java.util.ResourceBundle;

import io.resiliencebench.models.workload.configuration.cloud.CloudConfig;

/**
 * Builder class for CloudConfig to provide an immutable pattern for construction.
 */
public class CloudConfigBuilder {

	private String token;
	private String projectId;

	private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

	/**
	 * Sets the authentication token.
	 *
	 * @param token the authentication token
	 * @return the CloudConfigBuilder instance
	 */
	public CloudConfigBuilder withToken(String token) {
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException(bundle.getString("token.empty"));
		}
		this.token = token;
		return this;
	}

	/**
	 * Sets the project ID.
	 *
	 * @param projectId the project ID
	 * @return the CloudConfigBuilder instance
	 */
	public CloudConfigBuilder withProjectId(String projectId) {
		if (projectId == null || projectId.isEmpty()) {
			throw new IllegalArgumentException(bundle.getString("projectId.empty"));
		}
		this.projectId = projectId;
		return this;
	}

	/**
	 * Builds and returns a CloudConfig instance.
	 *
	 * @return the CloudConfig instance
	 */
	public CloudConfig build() {
		return new CloudConfig(token, projectId);
	}
}