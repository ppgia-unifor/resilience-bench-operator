package io.resiliencebench.models.enums;

/**
 * Enum representing the status messages for workloads.
 */
public enum WorkloadStatusMessages {
	CREATED("Workload has been created successfully."),
	PENDING("Workload has been created and is pending execution."),
	RUNNING("Workload is currently being executed."),
	COMPLETED("Workload execution has completed successfully."),
	FAILED("Workload execution has failed.");

	private final String message;

	WorkloadStatusMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
