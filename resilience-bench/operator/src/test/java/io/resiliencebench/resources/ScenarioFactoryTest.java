package io.resiliencebench.resources;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.resiliencebench.resources.benchmark.*;
import io.resiliencebench.resources.fault.DelayFault;
import io.resiliencebench.resources.workload.Workload;
import io.resiliencebench.resources.workload.WorkloadSpec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScenarioFactoryTest {


  public static ConnectorTemplate createConnector(
          String connectionName,
          List<Integer> maxAttempts,
          List<Integer> backoffLimit) {
    var source = new SourceTemplate("api-gateway",
            new NameValueProperties(
                    new NameValueProperties.Attribute("maxAttempts", maxAttempts),
                    new NameValueProperties.Attribute("backoffLimit", backoffLimit)
            )
    );
    var delay = new DelayFault(1000);
    var fault = new BenchmarkFaultTemplate(of(10), delay);
    var target = new TargetTemplate("service-x", fault);
    return new ConnectorTemplate(connectionName, source, target);
  }

  public static ConnectorTemplate createConnector(String connectionName) {
    return createConnector(connectionName, of(1, 2), of(100, 200));
  }

  public static Workload createWorkload(List<Integer> users) {
    var meta = new ObjectMeta();
    meta.setName("workload");
    var workload = new Workload();
    workload.setMetadata(meta);
    workload.setSpec(new WorkloadSpec(users, null));
    return workload;
  }

  @Test
  public void expandServiceParametersTest() {
    var parameters =
    ScenarioFactory.expandServiceParameters(
            new SourceTemplate("api-gateway",
                    new NameValueProperties(
                            new NameValueProperties.Attribute("maxAttempts", of(1, 2, 3)),
                            new NameValueProperties.Attribute("backoffLimit", of(1000, 2000, 3000))
                    )
            ));
    assertEquals(9, parameters.size());
  }

  @Test
  public void should_create_executions() {
    var spec = new BenchmarkSpec("workload",
            of(new ScenarioTemplate("scenario-1", of(createConnector("connector-1"))))
    );
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    var workload = createWorkload(of(10));
    var executions = ScenarioFactory.create(benchmark, workload);

    assertEquals(4, executions.size());
  }

  @Test
  public void should_create_executions_with_multiple_workloads() {
    var spec = new BenchmarkSpec("workload",
            of(new ScenarioTemplate("scenario-1", of(createConnector("connector-1"))))
    );
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    var workload = createWorkload(of(10, 20, 30));
    var executions = ScenarioFactory.create(benchmark, workload);

    assertEquals(12, executions.size());
  }

  @Test
  public void should_create_executions_with_multiple_scenarios() {
    var spec = new BenchmarkSpec("workload",
            of(
                    new ScenarioTemplate("scenario-1", of(createConnector("connector-1"))),
                    new ScenarioTemplate("scenario-2", of(createConnector("connector-2")))
            )
    );
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    var workload = createWorkload(of(10));
    var executions = ScenarioFactory.create(benchmark, workload);

    assertEquals(8, executions.size());
  }

  @Test
  public void should_create_executions_with_multiple_connectors() {
    var spec = new BenchmarkSpec("workload",
            of(new ScenarioTemplate("scenario-1", of(
                            createConnector("connector-1", of(1, 2, 3), of(100, 200)),
                            createConnector("connector-2", of(1, 2), of(100, 200, 300)))
                    )
            )
    );
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    var workload = createWorkload(of(10, 20));
    var generatedScenarios = ScenarioFactory.create(benchmark, workload);
    assertEquals(72, generatedScenarios.size());
    assertEquals("scenario-1-20vu-00036", generatedScenarios.get(71).getMetadata().getName());
  }

  @Test
  public void should_create_executions_with_multiple_scenarios_and_workloads() {
    var spec = new BenchmarkSpec("workload",
            of(
                    new ScenarioTemplate("scenario-1", of(createConnector("connector-1"))),
                    new ScenarioTemplate("scenario-2", of(createConnector("connector-2")))
            )
    );
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    var workload = createWorkload(of(10, 20, 30));
    var executions = ScenarioFactory.create(benchmark, workload);

    assertEquals(24, executions.size());
  }

  @Test
  public void should_create_executions_with_multiple_workloads_and_connectors() {
    var spec = new BenchmarkSpec("workload",
            of(new ScenarioTemplate("scenario-1", of(
                    createConnector("connector-1"),
                    createConnector("connector-2"))
                    )
            )
    );
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    var workload = createWorkload(of(10, 20, 30));
    var executions = ScenarioFactory.create(benchmark, workload);

    assertEquals(48, executions.size());
  }

  @Test
  public void should_create_executions_with_multiple_scenarios_workloads_and_connectors() {
    var spec = new BenchmarkSpec("workload",
            of(
                    new ScenarioTemplate("scenario-1", of(
                            createConnector("connector-1"),
                            createConnector("connector-2"))
                    ),
                    new ScenarioTemplate("scenario-2", of(
                            createConnector("connector-3"),
                            createConnector("connector-4"))
                    )
            )
    );
    var benchmark = new Benchmark();
    benchmark.setSpec(spec);
    var workload = createWorkload(of(10, 20, 30));
    var executions = ScenarioFactory.create(benchmark, workload);

    assertEquals(96, executions.size());
  }
}