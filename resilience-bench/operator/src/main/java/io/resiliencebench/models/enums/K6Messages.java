package io.resiliencebench.models.enums;

/**
 * Enum for log messages used within the K6 load generator step.
 */

public enum K6Messages {
	WORKLOAD_NOT_FOUND("Workload does not exist: %s"),
	JOB_CREATED("Job created: %s"),
	JOB_ALREADY_EXISTS("Job already exists: %s");

	private final String message;

	K6Messages(String message) {
		this.message = message;
	}

	public String format(Object... args) {
		return String.format(message, args);
	}

	@Override
	public String toString() {
		return message;
	}
}
