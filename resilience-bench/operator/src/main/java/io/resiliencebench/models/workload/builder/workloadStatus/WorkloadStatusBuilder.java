package io.resiliencebench.models.workload;

import java.time.LocalDateTime;

import io.resiliencebench.models.enums.ScenarioStatusEnum;

/**
 * Builder class for WorkloadStatus.
 */
public class WorkloadStatusBuilder {
	private ScenarioStatusEnum status;
	private String message;
	private LocalDateTime startedAt;
	private LocalDateTime completedAt;

	public WorkloadStatusBuilder withStatus(ScenarioStatusEnum status) {
		this.status = status;
		return this;
	}

	public WorkloadStatusBuilder withMessage(String message) {
		this.message = message;
		return this;
	}

	public WorkloadStatusBuilder withStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
		return this;
	}

	public WorkloadStatusBuilder withCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
		return this;
	}

	public WorkloadStatus build() {
		WorkloadStatus workloadStatus = new WorkloadStatus(status, message);
		workloadStatus.setStartedAt(startedAt);
		workloadStatus.setCompletedAt(completedAt);
		return workloadStatus;
	}
}