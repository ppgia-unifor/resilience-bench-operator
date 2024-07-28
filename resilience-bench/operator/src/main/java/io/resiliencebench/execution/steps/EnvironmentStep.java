package io.resiliencebench.execution.steps;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;

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
    var containerName = resilientService.getMetadata().getAnnotations().get("resiliencebench.io/container");

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

  public void applyNewVariables(Deployment deployment, String containerName, Map<String, JsonNode> env) {
    var container = deployment.getSpec().getTemplate().getSpec().getContainers().stream()
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
        variable.setValue(newValue.toString());
      }
    }

    kubernetesClient()
            .apps()
            .deployments()
            .resource(deployment)
            .update();

    logger.info("Waiting for the pods to restart");
    kubernetesClient()
            .apps()
            .deployments()
            .inNamespace(deployment.getMetadata().getNamespace())
            .withName(deployment.getMetadata().getName())
            .waitUntilCondition(this::waitUntilCondition, 2, TimeUnit.MINUTES);
    logger.info("Pods restarted successfully");
  }

  public boolean waitUntilCondition(Deployment deployment) {
    var pods = kubernetesClient().pods()
      .inNamespace(deployment.getMetadata().getNamespace())
      .withLabel("app", deployment.getMetadata().getName())
      .list()
      .getItems();

      return pods.stream().allMatch(pod -> {
          return pod.getStatus().getConditions().stream()
                  .anyMatch(condition -> "Ready".equals(condition.getType()) && "True".equals(condition.getStatus()));
      });
  }
}
