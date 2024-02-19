package br.unifor.ppgia.resiliencebench;

import br.unifor.ppgia.resiliencebench.resources.modeling.workload.Workload;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;

import java.util.Map;

public class K6TestRunner {

//  public void createCustomResource(String namespace, String crInstanceName, Map<String, Object> customResourceSpec) {
//    try (KubernetesClient client = new DefaultKubernetesClient()) {
//      // Define the Custom Resource instance as a generic Kubernetes resource
//      Map<String, Object> crMap = new HashMap<>();
//      crMap.put("apiVersion", "k6.io/v1alpha1"); // Replace with actual API version
//      crMap.put("kind", "K6");
//      crMap.put("metadata", Map.of("name", crInstanceName, "namespace", namespace));
//
//      // Add the spec if provided
//      if (customResourceSpec != null && !customResourceSpec.isEmpty()) {
//        crMap.put("spec", customResourceSpec);
//      }
//
//      // Convert the map to a generic Kubernetes resource
//      HasMetadata customResource = Serialization.unmarshal(Serialization.asJson(crMap), HasMetadata.class);
//
//      // Apply the Custom Resource instance to the Kubernetes cluster
//      Resource<HasMetadata> resource = client.resource(customResource);
//      resource.create();
//      System.out.println("Custom Resource created or updated successfully");
//    } catch (Exception e) {
//      e.printStackTrace();
//      System.err.println("Error creating Custom Resource: " + e.getMessage());
//    }
//  }



  public HasMetadata createResource(Workload workload) {
    var spec = Map.of(
            "parallelism", 1,
            "arguments", "--tag workloadName=" + workload.getMetadata().getName(),
            "script", Map.of(
                    "config", Map.of(
                            "name", workload.getSpec().getScript().getConfigMap().getName(),
                            "file", workload.getSpec().getScript().getConfigMap().getFile()
                    )
            )
    );

    var customResource = Map.of(
            "apiVersion", "k6.io/v1alpha1",
            "kind", "K6",
            "metadata", Map.of("name", workload.getMetadata().getName(), "namespace", workload.getMetadata().getNamespace()),
            "spec", spec
    );

    var resource = Serialization.unmarshal(Serialization.asJson(customResource), HasMetadata.class);
    KubernetesClient client = null;
    client.resource(resource).inNamespace(workload.getMetadata().getNamespace()).create();
    return resource;
  }



//  public void run(ScenarioWorkload scenarioWorkload) {
//    CustomResourceRepository<Workload> workloadRepository = new CustomResourceRepository(null, Workload.class);
//    var workload = workloadRepository.get("default", scenarioWorkload.getWorkloadName());
//
//    var meta = Map.of(
//            "apiVersion", "k6.io/v1alpha1",
//            "kind", "K6",
//            "metadata", Map.of("name", crInstanceName, "namespace", namespace)
//    );
//
//    var spec = Map.of(
//            "parallelism", 1,
//            "arguments", "--tag workloadName=" + scenarioWorkload.getWorkloadName(),
//            "script", Map.of(
//                    "config", Map.of(
//                            "name", workload.getSpec().getScript().getConfigMap().getName(),
//                            "file", workload.getSpec().getScript().getConfigMap().getFile()
//                    )
//            )
//    );
//  }
}
