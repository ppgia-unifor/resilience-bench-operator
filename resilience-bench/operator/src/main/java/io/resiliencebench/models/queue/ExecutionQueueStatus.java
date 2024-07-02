package io.resiliencebench.models.queue;

import java.time.LocalDateTime;

/**
 * Represents the status of an execution queue in the resilience bench system.
 */
public class ExecutionQueueStatus {

	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	/**
	 * Default constructor.
	 */
	public ExecutionQueueStatus() {
		// Default constructor required for deserialization
	}

	/**
	 * Constructs a new ExecutionQueueStatus with the specified parameters.
	 *
	 * @param status    the current status of the execution queue
	 * @param createdAt the creation timestamp of the execution queue
	 * @param updatedAt the last update timestamp of the execution queue
	 */
	public ExecutionQueueStatus(String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * Returns the current status of the execution queue.
	 *
	 * @return the current status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the current status of the execution queue.
	 *
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Returns the creation timestamp of the execution queue.
	 *
	 * @return the creation timestamp
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the creation timestamp of the execution queue.
	 *
	 * @param createdAt the timestamp to set
	 */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Returns the last update timestamp of the execution queue.
	 *
	 * @return the last update timestamp
	 */
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Sets the last update timestamp of the execution queue.
	 *
	 * @param updatedAt the timestamp to set
	 */
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "ExecutionQueueStatus{" +
				"status='" + status + '\'' +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				'}';
	}
}
