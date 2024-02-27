package br.unifor.ppgia.resiliencebench.resources.workload;

import br.unifor.ppgia.resiliencebench.resources.scenario.ScenarioWorkload;
import io.fabric8.kubernetes.api.model.HasMetadata;

public interface WorkloadResourceAdapter {

  HasMetadata adapt(Workload workload, ScenarioWorkload scenarioWorkload);
}
