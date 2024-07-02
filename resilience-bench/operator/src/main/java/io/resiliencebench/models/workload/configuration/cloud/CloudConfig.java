package io.resiliencebench.models.workload.configuration.cloud;

import java.util.Objects;

/**
 * Represents the cloud configuration containing the authentication token and project ID.
 */
public final class CloudConfig {

	private final String token;
	private final String projectId;

	public CloudConfig(String token, String projectId) {
		this.token = token;
		this.projectId = projectId;
	}

	/**
	 * Returns the authentication token.
	 *
	 * @return the authentication token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Returns the project ID.
	 *
	 * @return the project ID
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Provides a string representation of the CloudConfig.
	 *
	 * @return a string representation of the CloudConfig
	 */
	@Override
	public String toString() {
		return "CloudConfig{" +
				"token='" + token + '\'' +
				", projectId='" + projectId + '\'' +
				'}';
	}

	/**
	 * Checks whether this CloudConfig is equal to another object.
	 *
	 * @param o the object to compare to
	 * @return true if the objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CloudConfig that = (CloudConfig) o;
		return Objects.equals(token, that.token) &&
				Objects.equals(projectId, that.projectId);
	}

	/**
	 * Generates a hash code for this CloudConfig.
	 *
	 * @return an integer hash code value
	 */
	@Override
	public int hashCode() {
		return Objects.hash(token, projectId);
	}
}
