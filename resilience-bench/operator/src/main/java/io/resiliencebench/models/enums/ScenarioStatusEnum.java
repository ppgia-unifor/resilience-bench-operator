package io.resiliencebench.models.enums;

/**
 * Enum representing the status of a scenario.
 */
public enum ScenarioStatusEnum {
	CREATED("Created", "Scenario has been created successfully"),
	PENDING("Pending", "Scenario is pending execution"),
	RUNNING("Running", "Scenario is currently being executed"),
	COMPLETED("Completed", "Scenario execution has completed"),
	FAILED("Failed", "Scenario execution has failed");

	private final String status;
	private final String message;

	ScenarioStatusEnum(String status, String message) {
		this.status = status;
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}