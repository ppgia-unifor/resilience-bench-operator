package io.resiliencebench.models.enums;

/**
 * Enum representing the status of a queue item.
 */
public enum QueueItemStatus {
	FINISHED("finished"),
	RUNNING("running"),
	PENDING("pending");

	private final String status;

	QueueItemStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
