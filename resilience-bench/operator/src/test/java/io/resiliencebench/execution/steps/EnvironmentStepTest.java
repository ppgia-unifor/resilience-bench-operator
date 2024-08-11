package io.resiliencebench.execution.steps;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class EnvironmentStepTest {

    @Test
    void should_wait_when_pod_is_deleted() {
        var meta = new ObjectMeta();
        meta.setDeletionTimestamp("2021-08-01T00:00:00Z");
        var pod = new Pod();
        pod.setMetadata(meta);
        var environmentStep = new EnvironmentStep(null, null);
        var actual = environmentStep.waitUntilCondition(pod);
        assertFalse(actual);
    }
}