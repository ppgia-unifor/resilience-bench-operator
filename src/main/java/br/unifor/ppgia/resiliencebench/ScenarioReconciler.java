package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.execution.scenario.Scenario;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

@ControllerConfiguration
public class ScenarioReconciler implements Reconciler<Scenario> {

  private final static Logger logger = LoggerFactory.getLogger(ScenarioReconciler.class);

  @Override
  public UpdateControl<Scenario> reconcile(Scenario scenario, Context<Scenario> context) throws Exception {
    logger.info("Start: {}", scenario.getMetadata().getName());
    // Lógica para criar ou atualizar um Scenario
    if (deveProcessarScenario(scenario)) {
      if (existeJobEmExecucao(scenario.getMetadata().getNamespace(), context.getClient())) {
        // marcar como pendente para continuar aguardando a job finalizar
        atualizarStatusScenario(scenario, "pending", context.getClient());
      } else {
        criarJobParaScenario(scenario, context.getClient());
        atualizarStatusScenario(scenario, "processing", context.getClient());
      }
    }
    logger.info("End: {}", scenario.getMetadata().getName());
    return UpdateControl.noUpdate();
  }

  private boolean deveProcessarScenario(Scenario scenario) {
    return scenario.getSpec().getStatus().equals("pending");
  }

  private boolean existeJobEmExecucao(String namespace, KubernetesClient client) {
    var jobs = client.batch().v1().jobs().inNamespace(namespace).list();
    var deve = jobs.getItems().stream().anyMatch(job ->
            Objects.nonNull(job.getStatus().getCompletionTime()) && job.getMetadata().getAnnotations().containsKey("scenario"));
    logger.info("Deve processar: {}", deve);
    return deve;
  }

  private void criarJobParaScenario(Scenario scenario, KubernetesClient client) {
    Job job = new JobBuilder()
            .withApiVersion("batch/v1")
            .withNewMetadata()
            .withName(UUID.randomUUID().toString())
            .withNamespace(scenario.getMetadata().getNamespace())
            .addToAnnotations("scenario", scenario.getMetadata().getName())
            .endMetadata()
            .withNewSpec()
            .withBackoffLimit(4)
            .withNewTemplate()
            .withNewSpec()
            .withRestartPolicy("Never")
            .addNewContainer()
            .withName("kubectl")
            .withCommand("sleep", "10")
            .withImage("alpine")
            .endContainer()
            .endSpec()
            .endTemplate().and().build();

    job = client.batch().v1().jobs().resource(job).create();
    logger.info("Job criada: {}", job.getMetadata().getName());
  }

  private void atualizarStatusScenario(Scenario scenario, String status, KubernetesClient client) {
    scenario.getSpec().setStatus(status);
    client.resources(Scenario.class).inNamespace(scenario.getMetadata().getNamespace()).resource(scenario).update();

  }

}
