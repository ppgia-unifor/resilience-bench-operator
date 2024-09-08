package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.FilterWatchListDeletable;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

abstract class AbstractEnvironmentStep extends ExecutorStep<Deployment> {

  private final static Logger logger = LoggerFactory.getLogger(AbstractEnvironmentStep.class);
  protected final CustomResourceRepository<ResilientService> resilientServiceRepository;

  public AbstractEnvironmentStep(KubernetesClient kubernetesClient,
      CustomResourceRepository<ResilientService> resilientServiceRepository) {
    super(kubernetesClient);
    this.resilientServiceRepository = resilientServiceRepository;
  }

  protected Deployment getDeployment(Scenario scenario, ResilientService resilientService) {
    return kubernetesClient()
        .apps()
        .deployments()
        .inNamespace(scenario.getMetadata().getNamespace())
        .withLabelSelector(resilientService.getSpec().getSelector())
        .list()
        .getItems()
        .stream()
        .findFirst()
        .orElse(null);
  }

  protected void updateDeployment(Deployment targetDeployment) {
    kubernetesClient().apps().deployments()
        .inNamespace(targetDeployment.getMetadata().getNamespace())
        .resource(targetDeployment)
        .update();
  }

  protected void waitUntilReady(Deployment targetDeployment) {
    var newGeneration = targetDeployment.getMetadata().getGeneration();
    logger.info("Waiting for the deployment to restart");
    kubernetesClient().apps().deployments().inNamespace(targetDeployment.getMetadata().getNamespace())
        .withName(targetDeployment.getMetadata().getName())
        .waitUntilCondition((d -> d.getStatus().getObservedGeneration() >= newGeneration
            && d.getStatus().getReadyReplicas() != null
            && d.getStatus().getReadyReplicas().equals(d.getSpec().getReplicas())), 
            120, TimeUnit.SECONDS);

    logger.info("Pods restarted successfully");
  }

  protected List<EnvVar> getActualEnv(Deployment targetDeployment, String containerName) {
    var container = targetDeployment.getSpec().getTemplate().getSpec().getContainers().stream()
        .filter(c -> c.getName().equals(containerName))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Container not found: " + containerName));
    return container.getEnv();
  }

  public FilterWatchListDeletable<Pod, PodList, PodResource> getPods(Deployment targetDeployment) {
    return kubernetesClient().pods()
        .inNamespace(targetDeployment.getMetadata().getNamespace())
        .withLabel("app", targetDeployment.getMetadata().getName());
  }

  public boolean waitUntilCondition(Pod pod) {
    var match = pod.getMetadata().getDeletionTimestamp() == null &&
        pod
            .getStatus()
            .getConditions()
            .stream()
            .anyMatch(condition -> "Ready".equals(condition.getType()) && "True".equals(condition.getStatus()));

    logger.info("Pod {} is ready: {}", pod.getMetadata().getName(), match);
    return match;
  }
}