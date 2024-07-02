package io.resiliencebench.execution.istio.watcher;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.resiliencebench.models.enums.IstioMessages;
import io.resiliencebench.execution.istio.runner.ScenarioRunner;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.nonNull;

/**
 * Watcher for monitoring Kubernetes Job events.
 */
public class JobWatcher implements Watcher<Job> {

	private static final Logger logger = LoggerFactory.getLogger(JobWatcher.class);
	private final ScenarioRunner scenarioRunner;
	private final Scenario scenario;
	private final ExecutionQueue executionQueue;

	/**
	 * Constructs a new JobWatcher.
	 *
	 * @param scenarioRunner   the scenario runner
	 * @param scenario         the scenario being watched
	 * @param executionQueue   the execution queue
	 */
	public JobWatcher(ScenarioRunner scenarioRunner, Scenario scenario, ExecutionQueue executionQueue) {
		this.scenarioRunner = scenarioRunner;
		this.scenario = scenario;
		this.executionQueue = executionQueue;
	}

	@Override
	public void eventReceived(Action action, Job resource) {
		if (action == Action.MODIFIED && nonNull(resource.getStatus().getCompletionTime())) {
			handleJobCompletion(resource);
		}
	}

	private void handleJobCompletion(Job resource) {
		String namespace = resource.getMetadata().getNamespace();
		logger.debug(IstioMessages.FINISHED_JOB.format(resource.getMetadata().getName()));
		scenarioRunner.executePostScenarioSteps(scenario, executionQueue);
		scenarioRunner.runScenario(namespace, scenario.getMetadata().getName(), executionQueue);
	}

	@Override
	public void onClose(WatcherException cause) {
		logger.warn(IstioMessages.WATCHER_CLOSED.format(cause.getMessage()));
	}
}
