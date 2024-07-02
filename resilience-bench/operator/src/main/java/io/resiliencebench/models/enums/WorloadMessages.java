package io.resiliencebench.models.enums;

/**
 * Enum representing log messages for WorkloadStatus.
 */

public enum WorloadMessages {
	WORKLOAD_STATUS_INITIALIZED_PENDING("WorkloadStatus initialized with PENDING status"),
	WORKLOAD_STATUS_INITIALIZED("WorkloadStatus initialized with status: %s and message: %s"),
	WORKLOAD_STATUS_UPDATED("Workload status updated to: %s"),
	WORKLOAD_MESSAGE_UPDATED("Workload message updated to: %s");

	private final String message;

	WorloadMessages(String message) {
		this.message = message;
	}

	public String getMessage(Object... args) {
		return String.format(message, args);
	}
}
