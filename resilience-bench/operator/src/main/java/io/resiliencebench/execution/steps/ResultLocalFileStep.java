package io.resiliencebench.execution.steps;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Service
public class ResultLocalFileStep extends ExecutorStep {

  private final static Logger logger = LoggerFactory.getLogger(ResultLocalFileStep.class);

  public ResultLocalFileStep(KubernetesClient kubernetesClient) {
    super(kubernetesClient);
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return true;
  }

  @Override
  protected void internalExecute(Scenario scenario, ExecutionQueue executionQueue) {
    var executionQueueItem = executionQueue.getItem(scenario.getMetadata().getName());
    var currentResults = getFileString(executionQueueItem.getResultFile());
    if (currentResults.isPresent()) {
      var currentResultsJson = new JsonObject(currentResults.get());
      currentResultsJson.put("metadata", scenario.getSpec().toJson());

      var results = getFileString(executionQueue.getSpec().getResultFile());
      JsonObject resultsJson;
      if (results.isPresent()) {
        resultsJson = new JsonObject(results.get());
      } else {
        resultsJson = new JsonObject();
        resultsJson.put("results", new JsonArray());
      }
      resultsJson.getJsonArray("results").add(currentResultsJson);
      writeToFile(executionQueue.getSpec().getResultFile(), resultsJson.encode());
    }
  }

  private void writeToFile(String resultFile, String content) {
    try (var outputStream = new FileOutputStream(resultFile)) {
      outputStream.write(content.getBytes());
    } catch (IOException e) {
      logger.warn("Error writing file {}. {}", resultFile, e.getMessage());
    }
  }

  private Optional<String> getFileString(String resultFile) {
    try (var inputStream = new FileInputStream(resultFile)) {
      return of(new String(inputStream.readAllBytes()));
    } catch (IOException e) {
      logger.warn("Error reading file {}. {}", resultFile, e.getMessage());
      return empty();
    }
  }
}
