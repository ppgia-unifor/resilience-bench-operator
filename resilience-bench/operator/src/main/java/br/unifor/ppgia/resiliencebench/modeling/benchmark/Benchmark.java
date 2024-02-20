package br.unifor.ppgia.resiliencebench.modeling.benchmark;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.*;

@Group("resiliencebench.io")
@Version("v1beta1")
@ShortNames("bm")
@Plural("benchmarks")
@Kind("Benchmark")
public class Benchmark extends CustomResource<BenchmarkSpec, BenchmarkStatus> implements Namespaced {
}
