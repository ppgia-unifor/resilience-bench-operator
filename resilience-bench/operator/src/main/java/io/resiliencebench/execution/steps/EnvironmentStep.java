package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Environment;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EnvironmentStep extends ExecutorStep<Deployment> {

  private final static Logger logger = LoggerFactory.getLogger(EnvironmentStep.class);

  private final CustomResourceRepository<ResilientService> resilientServiceRepository;

  public EnvironmentStep(KubernetesClient kubernetesClient, CustomResourceRepository<ResilientService> resilientServiceRepository) {
    super(kubernetesClient);
    this.resilientServiceRepository = resilientServiceRepository;
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return scenario
            .getSpec()
            .getConnectors()
            .stream()
            .anyMatch(connector -> connector.getEnvironment() != null);
  }

  @Override
  protected Deployment internalExecute(Scenario scenario, ExecutionQueue queue) {
    for (var connector : scenario.getSpec().getConnectors()) {
      if (connector.getEnvironment() != null) {
        var env = connector.getEnvironment();
        var resilientService = resilientServiceRepository.get(scenario.getMetadata().getNamespace(), env.getApplyTo());

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
          // TODO support only one container per deployment
          applyNewVariables(deployment.get(), env);
          return deployment.get();
        } else {
          logger.warn("Deployment not found for ResilientService {}", env.getApplyTo());
        }
      }
    }
    return null;
  }

  private void applyNewVariables(Deployment deployment, Environment env) {
    var deploymentVars = deployment
            .getSpec()
            .getTemplate()
            .getSpec()
            .getContainers()
            .get(0)
            .getEnv()
            .stream()
            .toList();

    for (var variable : deploymentVars) {
      var newValue = env.getEnvs().get(variable.getName());
      if (newValue != null) {
        variable.setValue(newValue.toString());
      }
    }

    deployment.getSpec().getTemplate().getSpec().getContainers().get(0).setEnv(deploymentVars);
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
            .waitUntilCondition(pod -> pod.getStatus().getReadyReplicas().equals(pod.getStatus().getReplicas()), 1, TimeUnit.MINUTES);
    logger.info("Pods restarted successfully.");
  }
}
