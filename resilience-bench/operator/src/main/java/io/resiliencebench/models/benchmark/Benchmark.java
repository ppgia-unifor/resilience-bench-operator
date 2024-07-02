package io.resiliencebench.models.benchmark;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

/**
 * Represents a Benchmark custom resource in the Kubernetes cluster.
 * This resource is part of the resiliencebench.io API group and is used to define
 * benchmarking specifications and statuses.
 */
@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("bm")
@Plural("benchmarks")
@Kind("Benchmark")
public class Benchmark extends CustomResource<BenchmarkSpec, BenchmarkStatus> implements Namespaced {

	/**
	 * Default constructor for Benchmark.
	 * Required by the Kubernetes client for custom resources.
	 */
	public Benchmark() {
		// Default constructor required by Kubernetes client library
	}

	/**
	 * Constructor for Benchmark with specified spec and status.
	 *
	 * @param spec the specification of the benchmark
	 * @param status the status of the benchmark
	 */
	public Benchmark(BenchmarkSpec spec, BenchmarkStatus status) {
		setSpec(spec);
		setStatus(status);
	}

	@Override
	public String toString() {
		return "Benchmark{" +
				"spec=" + getSpec() +
				", status=" + getStatus() +
				", metadata=" + getMetadata() +
				'}';
	}
}
