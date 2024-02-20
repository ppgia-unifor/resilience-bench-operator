package br.unifor.ppgia.resiliencebench.modeling.workload;

import br.unifor.ppgia.resiliencebench.execution.scenario.ScenarioWorkload;
import io.fabric8.kubernetes.api.model.HasMetadata;

public interface WorkloadResourceAdapter {

  HasMetadata adapt(Workload workload, ScenarioWorkload scenarioWorkload);
}
