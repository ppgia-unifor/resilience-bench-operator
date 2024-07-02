package io.resiliencebench.execution.steps.resultFile;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.localFileManager.model.FileManager;
import io.resiliencebench.execution.steps.executor.ExecutorStep;
import io.resiliencebench.models.enums.ResultFileMessages;
import io.resiliencebench.models.queue.ExecutionQueue;
import io.resiliencebench.models.queue.QueueItem;
import io.resiliencebench.models.scenario.Scenario;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Service to handle result file updates for execution queue steps.
 */
@Service
public class ResultFileStep extends ExecutorStep<ExecutionQueue> {

  private static final Logger logger = LoggerFactory.getLogger(ResultFileStep.class);
  private final FileManager fileManager;

  @Value("${resiliencebench.resultFilePathTemplate:/results/%s.json}")
  private String resultFilePathTemplate;

  /**
   * Constructs a new ResultFileStep.
   *
   * @param kubernetesClient the Kubernetes client
   * @param fileManager      the file manager
   */
  public ResultFileStep(KubernetesClient kubernetesClient, FileManager fileManager) {
    super(kubernetesClient);
    this.fileManager = fileManager;
  }

  /**
   * Executes the result file update step for the given scenario and execution queue.
   *
   * @param scenario the scenario being executed
   * @param queue    the execution queue to update
   * @return the updated execution queue
   */
  @Override
  public ExecutionQueue execute(Scenario scenario, ExecutionQueue queue) {
    QueueItem queueItem = findQueueItem(queue, scenario)
            .orElseThrow(() -> new RuntimeException(ResultFileMessages.SCENARIO_NOT_FOUND.format()));

    updateResultFile(queueItem, scenario, queue);
    return queue;
  }

  /**
   * Finds the queue item associated with the given scenario.
   *
   * @param queue    the execution queue
   * @param scenario the scenario
   * @return an Optional containing the found QueueItem, or empty if not found
   */
  private Optional<QueueItem> findQueueItem(ExecutionQueue queue, Scenario scenario) {
    return queue.getSpec().getQueueItems().stream()
            .filter(item -> item.getScenario().equals(scenario.getMetadata().getName()))
            .findFirst();
  }

  /**
   * Updates the result file with the current results of the given scenario.
   *
   * @param queueItem the queue item containing the result file to update
   * @param scenario  the scenario being executed
   * @param queue     the execution queue
   */
  private void updateResultFile(QueueItem queueItem, Scenario scenario, ExecutionQueue queue) {
    getFileContent(queueItem.getResultFile())
            .ifPresent(currentResults -> {
              JsonObject resultsJson = getOrCreateResultsJson(queue.getSpec().getResultFile());
              addScenarioResults(resultsJson, scenario, currentResults);
              writeToFile(queue.getSpec().getResultFile(), resultsJson.encode());
              logger.info(ResultFileMessages.RESULT_FILE_UPDATED.format(queue.getSpec().getResultFile()));
            });
  }

  /**
   * Gets the content of a file as a String.
   *
   * @param filePath the path to the file
   * @return an Optional containing the file content, or empty if an error occurs
   */
  private Optional<String> getFileContent(String filePath) {
    try (FileInputStream inputStream = new FileInputStream(filePath)) {
      return Optional.of(new String(inputStream.readAllBytes()));
    } catch (IOException e) {
      logger.warn(ResultFileMessages.FILE_READ_ERROR.format(filePath, e.getMessage()));
      return Optional.empty();
    }
  }

  /**
   * Writes content to a file.
   *
   * @param filePath the path to the file
   * @param content  the content to write
   */
  private void writeToFile(String filePath, String content) {
    try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
      outputStream.write(content.getBytes());
    } catch (IOException e) {
      logger.warn(ResultFileMessages.FILE_WRITE_ERROR.format(filePath, e.getMessage()));
    }
  }

  /**
   * Gets the existing results JSON from a file, or creates a new JSON object if the file does not exist.
   *
   * @param resultFile the path to the result file
   * @return the results JSON object
   */
  private JsonObject getOrCreateResultsJson(String resultFile) {
    return getFileContent(resultFile)
            .map(JsonObject::new)
            .orElseGet(() -> new JsonObject().put("results", new JsonArray()));
  }

  /**
   * Adds the scenario results to the results JSON.
   *
   * @param resultsJson     the results JSON object
   * @param scenario        the scenario
   * @param currentResults  the current results as a JSON string
   */
  private void addScenarioResults(JsonObject resultsJson, Scenario scenario, String currentResults) {
    JsonObject currentResultsJson = new JsonObject(currentResults);
    currentResultsJson.put("metadata", scenario.getSpec().toJson());
    resultsJson.getJsonArray("results").add(currentResultsJson);
  }
}
