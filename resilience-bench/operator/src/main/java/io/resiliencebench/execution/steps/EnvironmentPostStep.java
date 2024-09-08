package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static io.resiliencebench.support.Annotations.CONTAINER;

@Service
public class EnvironmentPostStep extends AbstractEnvironmentStep {

  private final static Logger logger = LoggerFactory.getLogger(EnvironmentPostStep.class);

  public EnvironmentPostStep(KubernetesClient kubernetesClient,
      CustomResourceRepository<ResilientService> resilientServiceRepository) {
    super(kubernetesClient, resilientServiceRepository);
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

  @Override
  protected Deployment internalExecute(Scenario scenario, ExecutionQueue queue) {
    for (var connector : scenario.getSpec().getConnectors()) {
      restoreEnvironment(scenario, connector.getSource());
      restoreEnvironment(scenario, connector.getDestination());
    }
    return null;
  }

  public void restoreEnvironment(Scenario scenario, io.resiliencebench.resources.scenario.Service service) {
    var resilientService = resilientServiceRepository.get(scenario.getMetadata().getNamespace(), service.getName());
    var containerName = resilientService.getMetadata().getAnnotations().get(CONTAINER);

    var env = resilientService.getSpec().getEnvs();
    if (env == null) {
      return;
    }

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
      var container = deployment.get().getSpec().getTemplate().getSpec().getContainers().stream()
          .filter(c -> c.getName().equals(containerName))
          .findFirst()
          .orElseThrow(() -> new RuntimeException("Container not found: " + containerName));

      env.forEach(variable -> {
        logger.info("deployment {} container {}. envVar {}={}",
            deployment.get().getMetadata().getName(),
            containerName,
            variable.getName(),
            variable.getValue());
      });

      container.setEnv(env);
      updateDeployment(deployment.get());
      waitUntilReady(deployment.get());
    } else {
      logger.warn("Deployment not found for ResilientService {}", service.getName());
    }
  }
}
