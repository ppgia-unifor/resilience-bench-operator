package io.resiliencebench.models.enums;

/**
 * Enum representing log messages for scenario execution.
 */

public enum IstioMessages {
	QUEUE_NOT_FOUND("Queue not found: %s"),
	SCENARIO_NOT_FOUND("Scenario not found: %s.%s"),
	NO_ITEM_AVAILABLE("No item available for queue: %s"),
	ALL_ITEMS_FINISHED("All items finished for: %s"),
	RUNNING_SCENARIO("Running scenario: %s"),
	JOB_CREATED("Job created: %s"),
	FINISHED_JOB("Finished job: %s"),
	WATCHER_CLOSED("Watcher closed due to exception: %s"),
	SERVICE_NOT_FOUND("Service not found: %s.%s"),
	RESILIENT_SERVICE_NOT_FOUND("ResilientService not found for scenario: %s"),
	CIRCUIT_BREAKER_CONFIGURED("Istio Circuit Breaker configured for service: %s"),
	SCENARIO_AND_EXECUTION_QUEUE_NULL("Scenario and ExecutionQueue cannot be null"),
	FAULT_TEMPLATE_NULL("Fault template is null. No fault to configure."),
	CONFIGURING_FAULT_ON_TARGET("Configuring fault on target: %s in namespace: %s"),
	FAULT_CONFIGURED("Fault configured successfully for target: %s"),
	CONFIGURING_RETRY_ON_SOURCE("Configuring retry on source: %s in namespace: %s with target: %s"),
	RETRY_CONFIGURED("Retry configured successfully for source: %s"),
	RETRY_ATTEMPTS_INVALID("Retry attempts must be greater than or equal to 0."),
	RETRY_NOT_CONFIGURED("Retry not configured. Attempts and perTryTimeout are required for retry pattern configuration.");

	private final String message;

	IstioMessages(String message) {
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