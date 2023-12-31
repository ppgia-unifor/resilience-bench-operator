package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.ConfigMapReference;
import br.unifor.ppgia.resiliencebench.resources.workload.ScriptConfig;
import br.unifor.ppgia.resiliencebench.resources.workload.Workload;
import br.unifor.ppgia.resiliencebench.resources.workload.WorkloadSpec;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class K6TestRunnerTest {

  public static Workload createWorkload(List<Integer> users) {
    var workload = new Workload();
    workload.setMetadata(new ObjectMetaBuilder().withName("workload").withNamespace("default").build());
    var script = new ScriptConfig(new ConfigMapReference("k6-script", "script.js"));
    workload.setSpec(new WorkloadSpec(users, 100, null, script));
    return workload;
  }

//  @Test
//  public void testCreateCustomK6() {
//    K6TestRunner k6TestRunner = new K6TestRunner();
//    var workload = createWorkload(List.of(1, 2, 3));
//    var k6 = k6TestRunner.createResource(workload);
//    Assertions.assertEquals("default", k6.getMetadata().getNamespace());
//  }

}