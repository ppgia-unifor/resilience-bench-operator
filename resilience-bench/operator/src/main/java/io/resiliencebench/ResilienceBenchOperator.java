package io.resiliencebench;

import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.Operator;

public class ResilienceBenchOperator {

    public static void main(String[] args) {
        var client = new KubernetesClientBuilder().build();
        var operator = new Operator((overrider) -> overrider.withKubernetesClient(client));
        operator.register(new BenchmarkReconciler(client));
        operator.start();
    }
}
