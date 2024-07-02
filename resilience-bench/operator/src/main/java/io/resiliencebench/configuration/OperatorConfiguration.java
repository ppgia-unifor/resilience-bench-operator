package io.resiliencebench.configuration;

import io.fabric8.istio.client.DefaultIstioClient;
import io.fabric8.istio.client.IstioClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.resiliencebench.execution.steps.model.ScenarioExecutor;
import io.resiliencebench.execution.istio.IstioScenarioExecutor;
import io.resiliencebench.models.benchmark.Benchmark;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.scenario.Scenario;
import io.resiliencebench.models.service.ResilientService;
import io.resiliencebench.models.workload.Workload;
import io.resiliencebench.support.CustomResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for the Operator, setting up clients, repositories, and the operator itself.
 */
@Configuration
public class OperatorConfiguration {

  private static final Logger logger = LoggerFactory.getLogger(OperatorConfiguration.class);

  /**
   * Creates a KubernetesClient bean.
   *
   * @return a new instance of KubernetesClient
   */
  @Bean
  public KubernetesClient kubernetesClient() {
    return new KubernetesClientBuilder().build();
  }

  /**
   * Creates an IstioClient bean using the provided KubernetesClient.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @return a new instance of IstioClient
   */
  @Bean
  public IstioClient istioClient(final KubernetesClient kubernetesClient) {
    return new DefaultIstioClient(kubernetesClient);
  }

  /**
   * Creates an Operator bean and registers reconciler beans with it.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @param reconcilers      the list of Reconciler beans to register
   * @return a new instance of Operator
   */
  @Bean(destroyMethod = "stop")
  @ConditionalOnMissingBean(Operator.class)
  public Operator operator(final KubernetesClient kubernetesClient, final List<Reconciler<?>> reconcilers) {
    final Operator operator = createOperator(kubernetesClient, reconcilers);
    if (!reconcilers.isEmpty()) {
      operator.start();
    } else {
      logger.warn("No Reconcilers found in the application context: Not starting the Operator");
    }
    return operator;
  }

  /**
   * Creates an Operator instance and registers the provided reconcilers.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @param reconcilers      the list of Reconciler beans to register
   * @return a new instance of Operator
   */
  private Operator createOperator(final KubernetesClient kubernetesClient, final List<Reconciler<?>> reconcilers) {
    final Operator operator = new Operator(overrider -> overrider.withKubernetesClient(kubernetesClient));
    reconcilers.forEach(operator::register);
    return operator;
  }

  /**
   * Creates a ScenarioExecutor bean.
   *
   * @param istioScenarioExecutor the IstioScenarioExecutor to use
   * @return the provided IstioScenarioExecutor instance
   */
  @Bean
  public ScenarioExecutor scenarioExecutor(final IstioScenarioExecutor istioScenarioExecutor) {
    return istioScenarioExecutor;
  }

  /**
   * Creates a CustomResourceRepository bean for Scenario resources.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @return a new instance of CustomResourceRepository for Scenario resources
   */
  @Bean
  public CustomResourceRepository<Scenario> scenarioRepository(final KubernetesClient kubernetesClient) {
    return new CustomResourceRepository<>(kubernetesClient, Scenario.class);
  }

  /**
   * Creates a CustomResourceRepository bean for ExecutionQueue resources.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @return a new instance of CustomResourceRepository for ExecutionQueue resources
   */
  @Bean
  public CustomResourceRepository<ExecutionQueue> executionQueueRepository(final KubernetesClient kubernetesClient) {
    return new CustomResourceRepository<>(kubernetesClient, ExecutionQueue.class);
  }

  /**
   * Creates a CustomResourceRepository bean for Workload resources.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @return a new instance of CustomResourceRepository for Workload resources
   */
  @Bean
  public CustomResourceRepository<Workload> workloadRepository(final KubernetesClient kubernetesClient) {
    return new CustomResourceRepository<>(kubernetesClient, Workload.class);
  }

  /**
   * Creates a CustomResourceRepository bean for Benchmark resources.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @return a new instance of CustomResourceRepository for Benchmark resources
   */
  @Bean
  public CustomResourceRepository<Benchmark> benchmarkRepository(final KubernetesClient kubernetesClient) {
    return new CustomResourceRepository<>(kubernetesClient, Benchmark.class);
  }

  /**
   * Creates a CustomResourceRepository bean for ResilientService resources.
   *
   * @param kubernetesClient the KubernetesClient to use
   * @return a new instance of CustomResourceRepository for ResilientService resources
   */
  @Bean
  public CustomResourceRepository<ResilientService> resilientServiceRepository(final KubernetesClient kubernetesClient) {
    return new CustomResourceRepository<>(kubernetesClient, ResilientService.class);
  }
}
