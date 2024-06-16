package io.resiliencebench.execution;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.*;

@Service
public class ResultFileStep extends ExecutorStep<ExecutionQueue> {

  private final FileManager fileManager;
  private final static Logger logger = LoggerFactory.getLogger(ResultFileStep.class);

  public ResultFileStep(KubernetesClient kubernetesClient, FileManager fileManager) {
    super(kubernetesClient);
    this.fileManager = fileManager;
  }

  @Override
  public ExecutionQueue execute(Scenario scenario, ExecutionQueue queue) {
    var itemsStream = queue.getSpec().getItems().stream();
    var item = itemsStream
            .filter(i -> i.getScenario().equals(scenario.getMetadata().getName()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Scenario not found in queue"));

    var currentResults = getFileString(item.getResultFile());
    if (currentResults.isPresent()) {
      var results = getFileString(queue.getSpec().getResultFile());
      JsonObject resultsJson;
      if (results.isPresent()) {
        resultsJson = new JsonObject(results.get());
      } else {
        resultsJson = new JsonObject();
        resultsJson.put("results", new JsonArray());
      }
      var currentResultsJson = new JsonObject(currentResults.get());
      resultsJson.getJsonArray("results").add(currentResultsJson);
      writeToFile(queue.getSpec().getResultFile(), resultsJson.encode());
    }
    return queue;
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
