package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentStep extends ExecutorStep<Deployment> {
  public EnvironmentStep(KubernetesClient kubernetesClient) {
    super(kubernetesClient);
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return scenario.getSpec().getConnectors().stream().anyMatch(connector -> connector.getEnvironment() != null);
  }

  @Override
  protected Deployment internalExecute(Scenario scenario, ExecutionQueue queue) {
    return null;
  }

  public void updateDeploymentEnvVar(String namespace, String deploymentName, String envVarName, String newValue) {
    Deployment deployment = kubernetesClient().apps().deployments().inNamespace(namespace).withName(deploymentName).get();

    if (deployment != null) {
      DeploymentSpec deploymentSpec = deployment.getSpec();

      if (deploymentSpec != null && !deploymentSpec.getTemplate().getSpec().getContainers().isEmpty()) {
        deploymentSpec.getTemplate().getSpec().getContainers().get(0).getEnv().stream()
                .filter(envVar -> envVar.getName().equals(envVarName))
                .findFirst()
                .ifPresent(envVar -> envVar.setValue(newValue));

        kubernetesClient().apps().deployments().inNamespace(namespace).withName(deploymentName).replace(deployment);

        // Restarting pods
        kubernetesClient().pods().inNamespace(namespace)
                .withLabel("app", deploymentSpec.getSelector().getMatchLabels().get("app"))
                .delete();

        System.out.println("Environment variable updated and pods restarted successfully.");
      } else {
        System.out.println("No containers found in the deployment.");
      }
    } else {
      System.out.println("Deployment not found.");
    }
  }
}
