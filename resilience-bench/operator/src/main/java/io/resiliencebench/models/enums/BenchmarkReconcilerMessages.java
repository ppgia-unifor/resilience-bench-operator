package io.resiliencebench.models.enums;

/**
 * Enum to manage log messages in BenchmarkReconciler.
 */

public enum BenchmarkReconcilerMessages {
	WORKLOAD_NOT_FOUND("Workload not found: %s"),
	NO_SCENARIOS_GENERATED("No scenarios generated for benchmark %s"),
	BENCHMARK_RECONCILED("Benchmark %s reconciled. %d scenarios created"),
	CREATED_NEW_EXECUTION_QUEUE("Created new ExecutionQueue: %s"),
	SCENARIO_CREATED("Created new Scenario: {}"),
	SCENARIO_EXISTS("Scenario already exists: {}"),
	CLEANING_PREVIOUS_RESOURCES("Cleaning previous resources for benchmark %s"),
	SCENARIO_RECONCILIATION_STARTED("Starting reconciliation for benchmark %s"),
	SCENARIO_EXECUTION_STARTED("Starting execution of scenarios for benchmark %s"),
	UPDATED_RESILIENT_SERVICE_STATUS("Updated ResilientService status to '%s': %s");

	private final String message;

	BenchmarkReconcilerMessages(String message) {
		this.message = message;
	}

	public String format(Object... args) {
		return String.format(message, args);
	}
}