package io.resiliencebench.execution.steps;

import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.resiliencebench.support.Annotations.CONTAINER;

@Service
public class EnvironmentStep extends ExecutorStep<Deployment> {

  private final static Logger logger = LoggerFactory.getLogger(EnvironmentStep.class);

  private final CustomResourceRepository<ResilientService> resilientServiceRepository;

  public EnvironmentStep(KubernetesClient kubernetesClient,
                        CustomResourceRepository<ResilientService> resilientServiceRepository) {
    super(kubernetesClient);
    this.resilientServiceRepository = resilientServiceRepository;
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return scenario
            .getSpec()
            .getConnectors()
            .stream()
            .anyMatch(connector -> connector.getDestination().getEnvs() != null ||
                    connector.getSource().getEnvs() != null);
  }

  public void applyServiceEnvironment(Scenario scenario, io.resiliencebench.resources.scenario.Service service) {
    var env = service.getEnvs();
    var resilientService = resilientServiceRepository.get(scenario.getMetadata().getNamespace(), service.getName());
    var containerName = resilientService.getMetadata().getAnnotations().get(CONTAINER);

    var deployment = kubernetesClient()
            .apps()
            .deployments()
            .inNamespace(scenario.getMetadata().getNamespace())
            .withLabelSelector(resilientService.getSpec().getSelector())
            .list()
            .getItems()
            .stream()
            .findFirst();

    if (deployment.isPresent()) {
      applyNewVariables(deployment.get(), containerName, env);
    } else {
      logger.warn("Deployment not found for ResilientService {}", service.getName());
    }
  }

  @Override
  protected Deployment internalExecute(Scenario scenario, ExecutionQueue queue) {
    for (var connector : scenario.getSpec().getConnectors()) {
      applyServiceEnvironment(scenario, connector.getSource());
      applyServiceEnvironment(scenario, connector.getDestination());
    }
    return null;
  }

  public void applyNewVariables(Deployment targetDeployment, String containerName, Map<String, JsonNode> env) {
    var container = targetDeployment.getSpec().getTemplate().getSpec().getContainers().stream()
            .filter(c -> c.getName().equals(containerName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Container not found: " + containerName));
    var deploymentVars = container
            .getEnv()
            .stream()
            .toList();

    for (var variable : deploymentVars) {
      var newValue = env.get(variable.getName());
      if (newValue != null) {
        logger.info("Container {}. variable {} new value {}", containerName, variable.getName(), newValue);
        variable.setValue(newValue.toString());
      }
    }

    updateDeployment(targetDeployment);
    restartPods(targetDeployment);
  }

  private void updateDeployment(Deployment targetDeployment) {
    kubernetesClient().apps().deployments()
            .inNamespace(targetDeployment.getMetadata().getNamespace())
            .resource(targetDeployment)
            .update();
  }

  private void restartPods(Deployment targetDeployment) {
    logger.info("Waiting for the pods to restart");
    kubernetesClient().pods()
            .inNamespace(targetDeployment.getMetadata().getNamespace())
            .withLabel("app", targetDeployment.getMetadata().getName())
            .waitUntilCondition(this::waitUntilCondition, 60, TimeUnit.SECONDS);
    logger.info("Pods restarted successfully");
  }

  public boolean waitUntilCondition(Pod pod) {
    var isMarkedForDeletion = pod.getMetadata().getDeletionTimestamp() == null;
    if (!isMarkedForDeletion) return false;
    var isReady = pod.getStatus()
            .getConditions()
            .stream()
            .anyMatch(condition -> "Ready".equals(condition.getType()) && "True".equals(condition.getStatus()));
    if (isReady) {
      logger.info("Pod {} is ready", pod.getMetadata().getName());
    }

    return isReady;
  }
}
