package io.resiliencebench.execution.istio.runner;

import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.istio.watcher.JobWatcher;
import io.resiliencebench.models.enums.IstioMessages;
import io.resiliencebench.execution.steps.executor.ExecutorStep;
import io.resiliencebench.execution.k6.K6LoadGeneratorStep;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Runner responsible for executing Istio scenarios.
 */
public class ScenarioRunner {

	private static final Logger logger = LoggerFactory.getLogger(ScenarioRunner.class);
	private final KubernetesClient kubernetesClient;
	private final CustomResourceRepository<Scenario> scenarioRepository;
	private final List<ExecutorStep<?>> preparationSteps;
	private final List<ExecutorStep<?>> postScenarioExecutionSteps;
	private final K6LoadGeneratorStep k6LoadGeneratorStep;

	/**
	 * Constructs a new ScenarioRunner.
	 *
	 * @param kubernetesClient         the Kubernetes client
	 * @param scenarioRepository       the repository for scenarios
	 * @param preparationSteps         the list of steps to prepare the scenario
	 * @param postScenarioExecutionSteps the list of steps to execute after the scenario
	 * @param k6LoadGeneratorStep      the step to generate load using K6
	 */
	public ScenarioRunner(
			KubernetesClient kubernetesClient,
			CustomResourceRepository<Scenario> scenarioRepository,
			List<ExecutorStep<?>> preparationSteps,
			List<ExecutorStep<?>> postScenarioExecutionSteps,
			K6LoadGeneratorStep k6LoadGeneratorStep) {
		this.kubernetesClient = kubernetesClient;
		this.scenarioRepository = scenarioRepository;
		this.preparationSteps = preparationSteps;
		this.postScenarioExecutionSteps = postScenarioExecutionSteps;
		this.k6LoadGeneratorStep = k6LoadGeneratorStep;
	}

	/**
	 * Runs the specified scenario within the provided namespace and execution queue.
	 *
	 * @param namespace       the namespace of the scenario
	 * @param scenarioName    the name of the scenario
	 * @param executionQueue  the execution queue
	 */
	public void runScenario(String namespace, String scenarioName, ExecutionQueue executionQueue) {
		logger.info(IstioMessages.RUNNING_SCENARIO.format(scenarioName));
		Scenario scenario = scenarioRepository.find(namespace, scenarioName)
				.orElseThrow(() -> new RuntimeException(IstioMessages.SCENARIO_NOT_FOUND.format(namespace, scenarioName)));

		executePreparationSteps(scenario, executionQueue);
		Job job = createAndWatchJob(scenario, executionQueue);
		logger.info(IstioMessages.JOB_CREATED.format(job.getMetadata().getName()));
	}

	private void executePreparationSteps(Scenario scenario, ExecutionQueue executionQueue) {
		preparationSteps.forEach(step -> step.execute(scenario, executionQueue));
	}

	private Job createAndWatchJob(Scenario scenario, ExecutionQueue executionQueue) {
		Job job = k6LoadGeneratorStep.execute(scenario, executionQueue);
		var jobsClient = kubernetesClient.batch().v1().jobs();
		job = jobsClient.resource(job).create();
		jobsClient.resource(job).watch(new JobWatcher(this, scenario, executionQueue));
		return job;
	}

	/**
	 * Executes post-scenario steps for the given scenario and execution queue.
	 *
	 * @param scenario         the scenario
	 * @param executionQueue   the execution queue
	 */
	public void executePostScenarioSteps(Scenario scenario, ExecutionQueue executionQueue) {
		postScenarioExecutionSteps.forEach(step -> step.execute(scenario, executionQueue));
	}
}