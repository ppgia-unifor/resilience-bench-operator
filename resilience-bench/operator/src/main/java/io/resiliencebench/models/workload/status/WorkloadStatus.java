package io.resiliencebench.models.workload.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.resiliencebench.models.enums.ScenarioStatusEnum;
import io.resiliencebench.models.enums.WorloadMessages;
import io.resiliencebench.models.workload.builder.workloadStatus.WorkloadStatusBuilder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Represents the status of a workload in the resilience benchmark.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize
@JsonDeserialize
public class WorkloadStatus {

	private static final Logger logger = Logger.getLogger(WorkloadStatus.class.getName());

	private ScenarioStatusEnum status;
	private String message;
	private LocalDateTime lastUpdated;
	private LocalDateTime startedAt;
	private LocalDateTime completedAt;

	/**
	 * Default constructor for WorkloadStatus.
	 */
	public WorkloadStatus() {
		this.status = ScenarioStatusEnum.PENDING;
		this.message = ScenarioStatusEnum.PENDING.getMessage();
		this.lastUpdated = LocalDateTime.now();
		logger.info(WorloadMessages.WORKLOAD_STATUS_INITIALIZED_PENDING.getMessage());
	}

	/**
	 * Constructs a WorkloadStatus with the specified status and message.
	 *
	 * @param status  the status of the workload
	 * @param message the message related to the status
	 */
	public WorkloadStatus(ScenarioStatusEnum status, String message) {
		this.status = status;
		this.message = message;
		this.lastUpdated = LocalDateTime.now();
		logger.info(WorloadMessages.WORKLOAD_STATUS_INITIALIZED.getMessage(status, message));
	}

	/**
	 * Returns the status of the workload.
	 *
	 * @return the status of the workload
	 */
	public ScenarioStatusEnum getStatus() {
		return status;
	}

	/**
	 * Sets the status of the workload.
	 *
	 * @param status the new status of the workload
	 * @throws IllegalArgumentException if the status transition is invalid
	 */
	public void setStatus(ScenarioStatusEnum status) {
		validateStatusTransition(this.status, status);
		this.status = status;
		this.message = status.getMessage();
		this.lastUpdated = LocalDateTime.now();
		logger.info(WorloadMessages.WORKLOAD_STATUS_UPDATED.getMessage(status));
	}

	/**
	 * Returns the message related to the status.
	 *
	 * @return the message related to the status
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message related to the status.
	 *
	 * @param message the new message related to the status
	 */
	public void setMessage(String message) {
		this.message = message;
		this.lastUpdated = LocalDateTime.now();
		logger.info(WorloadMessages.WORKLOAD_MESSAGE_UPDATED.getMessage(message));
	}

	/**
	 * Returns the timestamp when the status was last updated.
	 *
	 * @return the timestamp when the status was last updated
	 */
	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * Sets the timestamp when the status was last updated.
	 *
	 * @param lastUpdated the new timestamp when the status was last updated
	 */
	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * Returns the timestamp when the workload started.
	 *
	 * @return the timestamp when the workload started
	 */
	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	/**
	 * Sets the timestamp when the workload started.
	 *
	 * @param startedAt the new timestamp when the workload started
	 */
	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}

	/**
	 * Returns the timestamp when the workload completed.
	 *
	 * @return the timestamp when the workload completed
	 */
	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	/**
	 * Sets the timestamp when the workload completed.
	 *
	 * @param completedAt the new timestamp when the workload completed
	 */
	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	/**
	 * Validates the transition between statuses.
	 *
	 * @param currentStatus the current status of the workload
	 * @param newStatus     the new status to transition to
	 * @throws IllegalArgumentException if the transition is invalid
	 */
	private void validateStatusTransition(ScenarioStatusEnum currentStatus, ScenarioStatusEnum newStatus) {
		if (currentStatus == ScenarioStatusEnum.COMPLETED || currentStatus == ScenarioStatusEnum.FAILED) {
			throw new IllegalArgumentException("Cannot transition from " + currentStatus + " to " + newStatus);
		}
		// Additional validation logic can be added here
	}

	@Override
	public String toString() {
		return "WorkloadStatus{" +
				"status=" + status +
				", message='" + message + '\'' +
				", lastUpdated=" + lastUpdated +
				", startedAt=" + startedAt +
				", completedAt=" + completedAt +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		WorkloadStatus that = (WorkloadStatus) o;
		return status == that.status &&
				Objects.equals(message, that.message) &&
				Objects.equals(lastUpdated, that.lastUpdated) &&
				Objects.equals(startedAt, that.startedAt) &&
				Objects.equals(completedAt, that.completedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(status, message, lastUpdated, startedAt, completedAt);
	}

	/**
	 * Returns a new instance of the WorkloadStatusBuilder.
	 *
	 * @return a new instance of WorkloadStatusBuilder
	 */
	public static WorkloadStatusBuilder newBuilder() {
		return new WorkloadStatusBuilder();
	}
}
