package br.unifor.ppgia.resiliencebench.k6;

import br.unifor.ppgia.resiliencebench.resources.ConfigMapReference;
import br.unifor.ppgia.resiliencebench.resources.execution.scenario.ScenarioWorkload;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.ScriptConfig;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.Workload;
import br.unifor.ppgia.resiliencebench.resources.modeling.workload.WorkloadSpec;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class K6WorkloadAdapterTest {

  @Test
  public void testAdapt() {
    var workload = new Workload();
    var config = new ScriptConfig(new ConfigMapReference("configMap", "file"));
    var workloadSpec = new WorkloadSpec(Lists.list(10, 20), 5000, "http://test.com/status/200", config);
    workload.setSpec(workloadSpec);
    workload.setMetadata(new ObjectMetaBuilder().withName("test").withNamespace("default").build());

    var scenarioWorkload = new ScenarioWorkload();
    scenarioWorkload.setUsers(10);
    scenarioWorkload.setWorkloadName("test");

    var adapter = new K6WorkloadAdapter();
    var actual = adapter.adapt(workload, scenarioWorkload);

    assertEquals(actual.getMetadata().getName(), workload.getMetadata().getName());
    assertEquals(actual.getMetadata().getNamespace(), workload.getMetadata().getNamespace());
    assertEquals(actual.getKind(), "TestRun");
    assertEquals(actual.getApiVersion(), "k6.io/v1alpha1");

    var spec = (Map<String, Object>)((GenericKubernetesResource) actual).getAdditionalProperties().get("spec");
    assertEquals(spec.get("parallelism"), 1);
    assertEquals(spec.get("arguments"), "--vus 10 --tag workloadName=test --duration 5000s");
    var script = (Map<String, Object>) spec.get("script");
    var scriptConfig = (Map<String, Object>) script.get("config");
    assertEquals(scriptConfig.get("name"), "configMap");
    assertEquals(scriptConfig.get("file"), "file");
  }
}