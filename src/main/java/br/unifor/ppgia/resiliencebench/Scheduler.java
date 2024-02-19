package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.execution.queue.ExecutionQueue;
import br.unifor.ppgia.resiliencebench.execution.queue.Item;
import br.unifor.ppgia.resiliencebench.execution.scenario.Scenario;
import br.unifor.ppgia.resiliencebench.support.Annotations;
import br.unifor.ppgia.resiliencebench.support.CustomResourceRepository;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.UUID;

public class Scheduler {

  private final static Logger logger = LoggerFactory.getLogger(Scheduler.class);
  private final KubernetesClient client;

  private CustomResourceRepository<ExecutionQueue> queueCustomResourceRepository;

  public Scheduler(KubernetesClient client) {
    this.client = client;
  }

  private Item getNextItem(ExecutionQueue queue) {
    var items = queue.getSpec().getItems();
    var nextItem = items.stream().filter(item -> !item.isFinished()).findFirst();
    return nextItem.orElse(null);
  }

  public void run(ExecutionQueue queue) {
    var nextItem = getNextItem(queue);
    if (nextItem != null) {
      var scenario = new CustomResourceRepository<>(client, Scenario.class).get(queue.getMetadata().getNamespace(), nextItem.getScenario()).get();
      schedule(scenario);
    }
  }

  private void schedule(Scenario scenario) {
    logger.debug("Start: {}", scenario.getMetadata().getName());

    queueCustomResourceRepository = new CustomResourceRepository<>(client, ExecutionQueue.class);
    var benchmark = scenario.getMetadata().getAnnotations().get(Annotations.OWNED_BY);
    var executionQueue = queueCustomResourceRepository.get(scenario.getMetadata().getNamespace(), benchmark).get(); // TODO tratar erro

    // Lógica para criar ou atualizar um Scenario
    if (deveProcessarScenario(scenario, executionQueue)) {
      if (!existeJobEmExecucao(scenario.getMetadata().getNamespace())) {
        criarJobParaScenario(scenario);
        atualizarStatusScenario(scenario, "processing", executionQueue);
      }
    }
    logger.info("End: {}", scenario.getMetadata().getName());
  }

  private boolean deveProcessarScenario(Scenario scenario, ExecutionQueue executionQueue) {
    var items = executionQueue.getSpec().getItems();
    var scenarioName = scenario.getMetadata().getName();

    var foundItem = items.stream().filter(item -> item.getScenario().equals(scenarioName)).findFirst();
    return foundItem.map(Item::isPending).orElse(false);
  }

  private boolean existeJobEmExecucao(String namespace) {
    var jobs = client.batch().v1().jobs().inNamespace(namespace).list();
    var deve = jobs.getItems().stream().anyMatch(job ->
            Objects.isNull(job.getStatus().getCompletionTime()) && job.getMetadata().getAnnotations().containsKey("scenario"));
    logger.info("Deve processar: {}", deve);
    return deve;
  }

  private void criarJobParaScenario(Scenario scenario) {
    var jobName = UUID.randomUUID().toString();
    var namespace = scenario.getMetadata().getNamespace();
    var job = new JobBuilder()
            .withApiVersion("batch/v1")
            .withNewMetadata()
            .withName(jobName)
            .withNamespace(namespace)
            .addToAnnotations("scenario", scenario.getMetadata().getName())
            .endMetadata()
            .withNewSpec()
            .withBackoffLimit(4)
            .withNewTemplate()
            .withNewSpec()
            .withRestartPolicy("Never")
            .addNewContainer()
            .withName("kubectl")
            .withCommand("sleep", "3")
            .withImage("alpine")
            .endContainer()
            .endSpec()
            .endTemplate().and().build();

    job = client.batch().v1().jobs().resource(job).create();

    client.batch().v1().jobs().resource(job).watch(new Watcher<>() {
      @Override
      public void eventReceived(Action action, Job resource) {
        if (action.equals(Action.MODIFIED)) {
          if (Objects.nonNull(resource.getStatus().getCompletionTime())) {
            logger.info("Job finalizado: {}", resource.getMetadata().getName());
            var scenarioName = resource.getMetadata().getAnnotations().get("scenario");
            var scenario = new CustomResourceRepository<>(client, Scenario.class).get(namespace, scenarioName).get();
            var executionQueue = new CustomResourceRepository<>(client, ExecutionQueue.class).get(namespace, scenario.getMetadata().getAnnotations().get(Annotations.OWNED_BY)).get();
            atualizarStatusScenario(scenario, "finished", executionQueue);
            run(executionQueue);
          }
        }
      }

      @Override
      public void onClose(WatcherException cause) {

      }
    });
    logger.debug("Job criada: {}", job.getMetadata().getName());
  }

  private void atualizarStatusScenario(Scenario scenario, String status, ExecutionQueue executionQueue) {
    executionQueue.getSpec().getItems().stream()
            .filter(item -> item.getScenario().equals(scenario.getMetadata().getName()))
            .findFirst()
            .ifPresent(item -> {
              executionQueue.getMetadata().setNamespace(scenario.getMetadata().getNamespace());
              item.setStatus(status);
              item.setStartedAt(LocalDateTime.now().atZone(ZoneId.of("UTC")).toString());
              queueCustomResourceRepository.update(executionQueue);
            });
  }
}
