package io.resiliencebench.support;

/**
 * Interface representing common annotation keys used within the application.
 */
public interface Annotations {

  String OWNED_BY = "resiliencebench.io/owned-by";
  String CREATED_BY = "resiliencebench.io/created-by";
  String SCENARIO = "resiliencebench.io/scenario";
  String VIRTUAL_SERVICE = "resiliencebench.io/virtual-service";
  String WORKLOAD = "resiliencebench.io/workload";
  String ISTIO_SIDECAR_INJECT = "sidecar.istio.io/inject";
}
