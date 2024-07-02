package io.resiliencebench.models.enums;

/**
 * Enum representing log messages for file operations.
 */

public enum ResultFileMessages {
	SCENARIO_NOT_FOUND("Scenario not found in queue"),
	FILE_WRITE_ERROR("Error writing file %s. %s"),
	FILE_READ_ERROR("Error reading file %s. %s"),
	RESULT_FILE_UPDATED("Result file %s successfully updated");

	private final String message;

	ResultFileMessages(String message) {
		this.message = message;
	}

	public String format(Object... args) {
		return String.format(message, args);
	}
}