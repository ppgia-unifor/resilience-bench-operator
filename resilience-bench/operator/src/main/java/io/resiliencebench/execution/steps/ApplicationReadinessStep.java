package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.resiliencebench.resources.service.ResilientService;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ApplicationReadinessStep extends AbstractEnvironmentStep {

  private final static Logger logger = LoggerFactory.getLogger(ApplicationReadinessStep.class);

  public ApplicationReadinessStep(KubernetesClient kubernetesClient, CustomResourceRepository<ResilientService> resilientServiceRepository) {
    super(kubernetesClient, resilientServiceRepository);
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return true;
  }

  @Override
  protected void internalExecute(Scenario scenario, ExecutionQueue queue) {
    var ns = scenario.getMetadata().getNamespace();
    resilientServiceRepository.list(ns).forEach(resilientService -> {
      var deployment = getDeployment(scenario, resilientService);
      if (deployment != null) {
        logger.info("Waiting for deployment: {}", deployment.getMetadata().getName());
        getPods(deployment).waitUntilCondition(this::waitUntilCondition, 2, TimeUnit.MINUTES);
        waitUntilReady(deployment);
      }
    });
  }
}
