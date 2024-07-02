package io.resiliencebench.models.enums;

/**
 * Enum for managing log messages.
 */

public enum CustomResourceRepositoryMessages {
	CREATING_RESOURCE("Creating resource: {}"),
	UPDATING_RESOURCE("Updating resource: {}, version: {}"),
	UPDATING_STATUS("Updating status for resource: {}"),
	PATCHING_STATUS("Patching status for resource: {}"),
	DELETING_RESOURCE("Deleting resource: {}"),
	RESOURCE_NOT_FOUND("Resource %s.%s not found");

	private final String message;

	CustomResourceRepositoryMessages(String message) {
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