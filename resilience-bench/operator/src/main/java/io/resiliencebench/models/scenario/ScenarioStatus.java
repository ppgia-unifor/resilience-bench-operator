package io.resiliencebench.models.scenario;

import java.time.LocalDateTime;
import java.util.Objects;

import io.resiliencebench.models.enums.ScenarioStatusEnum;

/**
 * Represents the status of a scenario in the resilience benchmark.
 */
public class ScenarioStatus {

	private ScenarioStatusEnum statusEnum;
	private String status;
	private String message;
	private LocalDateTime timestamp;

	/**
	 * Default constructor for ScenarioStatus.
	 */
	public ScenarioStatus() {
		this.timestamp = LocalDateTime.now();
	}

	/**
	 * Constructs a ScenarioStatus with the specified status and message.
	 *
	 * @param statusEnum the enum representing the status of the scenario
	 */
	public ScenarioStatus(ScenarioStatusEnum statusEnum) {
		this.statusEnum = statusEnum;
		this.status = statusEnum.getStatus();
		this.message = statusEnum.getMessage();
		this.timestamp = LocalDateTime.now();
	}

	/**
	 * Returns the status enum of the scenario.
	 *
	 * @return the status enum of the scenario
	 */
	public ScenarioStatusEnum getStatusEnum() {
		return statusEnum;
	}

	/**
	 * Sets the status enum of the scenario.
	 *
	 * @param statusEnum the new status enum of the scenario
	 */
	public void setStatusEnum(ScenarioStatusEnum statusEnum) {
		this.statusEnum = statusEnum;
		this.status = statusEnum.getStatus();
		this.message = statusEnum.getMessage();
	}

	/**
	 * Returns the status of the scenario.
	 *
	 * @return the status of the scenario
	 */
	public String getStatus() {
		return status;
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
	 * Returns the timestamp when the status was last updated.
	 *
	 * @return the timestamp when the status was last updated
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp when the status was last updated.
	 *
	 * @param timestamp the new timestamp when the status was last updated
	 */
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns a string representation of the ScenarioStatus.
	 *
	 * @return a string representation of the ScenarioStatus
	 */
	@Override
	public String toString() {
		return "ScenarioStatus{" +
				"statusEnum=" + statusEnum +
				", status='" + status + '\'' +
				", message='" + message + '\'' +
				", timestamp=" + timestamp +
				'}';
	}

	/**
	 * Compares this ScenarioStatus to another object.
	 *
	 * @param o the object to compare to
	 * @return true if the objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ScenarioStatus that = (ScenarioStatus) o;
		return statusEnum == that.statusEnum &&
				Objects.equals(status, that.status) &&
				Objects.equals(message, that.message) &&
				Objects.equals(timestamp, that.timestamp);
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(statusEnum, status, message, timestamp);
	}
}
