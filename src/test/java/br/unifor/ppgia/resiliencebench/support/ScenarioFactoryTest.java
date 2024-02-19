package br.unifor.ppgia.resiliencebench.support;

import br.unifor.ppgia.resiliencebench.execution.ScenarioFactory;
import br.unifor.ppgia.resiliencebench.fault.DelayFault;
import br.unifor.ppgia.resiliencebench.modeling.benchmark.*;
import br.unifor.ppgia.resiliencebench.modeling.workload.Workload;
import br.unifor.ppgia.resiliencebench.modeling.workload.WorkloadSpec;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScenarioFactoryTest {

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
            new PatternConfig(
                    new PatternConfig.Attribute("maxAttempts", List.of(1, 2, 3)),
                    new PatternConfig.Attribute("backoffLimit", List.of(1000, 2000, 3000))
            )
    );
    var delay = new DelayFault(1000);
    var fault = new BenchmarkFaultTemplate(List.of(10), delay);
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
                    new PatternConfig(
                            new PatternConfig.Attribute("maxAttempts", List.of(1, 2, 3)),
                            new PatternConfig.Attribute("backoffLimit", List.of(1000, 2000, 3000))
                    )
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