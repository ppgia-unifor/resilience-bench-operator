package io.resiliencebench.resources.workload;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.resiliencebench.resources.scenario.ScenarioWorkload;

public interface WorkloadResourceAdapter {

  HasMetadata adapt(Workload workload, ScenarioWorkload scenarioWorkload);
}
