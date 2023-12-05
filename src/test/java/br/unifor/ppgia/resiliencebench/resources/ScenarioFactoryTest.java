package br.unifor.ppgia.resiliencebench.resources;

import br.unifor.ppgia.resiliencebench.resources.benchmark.*;
import br.unifor.ppgia.resiliencebench.resources.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import br.unifor.ppgia.resiliencebench.resources.workload.WorkloadSpec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScenarioFactoryTest {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static JsonNode createIntArrayJsonNode(String propertyName, List<Integer> content) {
    var jsonNode = objectMapper.createObjectNode();
    var arrayNode = jsonNode.putArray(propertyName);
    content.forEach(arrayNode::add);
    return jsonNode;
  }

  public static JsonNode createIntArrayJsonNode(Map<String, List<Integer>> content) {
    var jsonNode = objectMapper.createObjectNode();
    for (var entry : content.entrySet()) {
      var arrayNode = jsonNode.putArray(entry.getKey());
      entry.getValue().forEach(arrayNode::add);
    }
    return jsonNode;
  }

  public static Benchmark createBenchmark() {
    var connections = new ArrayList<Connection>();
    connections.add(createConnection("connection1"));
    connections.add(createConnection("connection2"));
    var spec = new BenchmarkSpec(5, "workload", connections);
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    return benchmark;
  }

  public static Connection createConnection(String connectionName) {
    var source = new Source("api-gateway",
            Map.of("patternConfig",
                    createIntArrayJsonNode(
                            Map.of("maxAttempts", List.of(1, 2, 3), "backoffLimit", List.of(1000, 2000, 3000))
    )));

    var delay = new DelayFault(1000);
    var fault = new BenchmarkFaultTemplate(asList(10), delay);
    var target = new Target("vets-service", fault);
    return new Connection(connectionName, source, target);
  }

  public static Workload createWorkload(List<Integer> users) {
    var meta = new ObjectMeta();
    meta.setName("workload");
    var workload = new Workload();
    workload.setMetadata(meta);
    workload.setSpec(new WorkloadSpec(users, 100, null, null));
    return workload;
  }

  @Test
  public void expandServiceParametersTest() {
    var parameters =
    ScenarioFactory.expandServiceParameters(
            new Source("api-gateway",
                    Map.of("patternConfig",
                            createIntArrayJsonNode(Map.of(
                                    "maxAttempts", List.of(1, 2, 3),
                                    "backoffLimit", List.of(1000, 2000, 3000))
                            ))
    ));
    assertEquals(9, parameters.size());
  }

  @Test
  public void should_create_correct_number_of_scenarios() {
    var benchmark = createBenchmark();
    var workload = createWorkload(of(10, 20, 30));
    var scenarios = ScenarioFactory.create(benchmark, workload);
    assertEquals(54, scenarios.size());
  }
}