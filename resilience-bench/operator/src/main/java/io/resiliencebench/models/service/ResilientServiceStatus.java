package io.resiliencebench.models.service;

import java.util.Objects;

/**
 * Represents the status of a ResilientService in the resilience benchmark.
 */
public class ResilientServiceStatus {
	private String status;
	private String message;

	/**
	 * Default constructor for ResilientServiceStatus.
	 */
	public ResilientServiceStatus() {
	}

	/**
	 * Constructs a ResilientServiceStatus with the specified status and message.
	 *
	 * @param status  the status of the ResilientService
	 * @param message the message related to the status
	 */
	public ResilientServiceStatus(String status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * Returns the status of the ResilientService.
	 *
	 * @return the status of the ResilientService
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status of the ResilientService.
	 *
	 * @param status the new status of the ResilientService
	 */
	public void setStatus(String status) {
		this.status = status;
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
	}

	/**
	 * Returns a string representation of the ResilientServiceStatus.
	 *
	 * @return a string representation of the ResilientServiceStatus
	 */
	@Override
	public String toString() {
		return "ResilientServiceStatus{" +
				"status='" + status + '\'' +
				", message='" + message + '\'' +
				'}';
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @param o the reference object with which to compare
	 * @return true if this object is the same as the obj argument; false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ResilientServiceStatus that = (ResilientServiceStatus) o;
		return Objects.equals(status, that.status) &&
				Objects.equals(message, that.message);
	}

	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(status, message);
	}
}
