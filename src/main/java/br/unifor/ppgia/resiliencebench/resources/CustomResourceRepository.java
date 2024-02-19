package br.unifor.ppgia.resiliencebench.resources;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Replaceable;
import io.fabric8.kubernetes.client.dsl.Resource;

import java.util.List;
import java.util.Optional;

public class CustomResourceRepository<T extends CustomResource> {

  private final KubernetesClient kubernetesClient;
  private final Class<T> resourceClass;
  private final MixedOperation<T, KubernetesResourceList<T>, Resource<T>> resources;

  public CustomResourceRepository(KubernetesClient kubernetesClient, Class<T> resourceClass) {
    this.kubernetesClient = kubernetesClient;
    this.resourceClass = resourceClass;
    this.resources = kubernetesClient.resources(resourceClass);
  }

  private NonNamespaceOperation<T, KubernetesResourceList<T>, Resource<T>> inNamespace(T resource) {
    return this.resources.inNamespace(resource.getMetadata().getNamespace());
  }

  public T create(T resource) {
    return inNamespace(resource).resource(resource).create();
  }

  public T update(T resource) {
    return inNamespace(resource).resource(resource).update();
  }

  public void delete(T resource) {
    inNamespace(resource).resource(resource).delete();
  }

  public Optional<T> get(ObjectMeta meta) {
    return this.get(meta.getNamespace(), meta.getName());
  }

  public Optional<T> get(String namespace, String name) {
    return Optional.ofNullable(
            this.resources.inNamespace(namespace).withName(name).get()
    );
  }

  public List<T> list(String namespace) {
    return this.resources.inNamespace(namespace).list().getItems();
  }

//  public boolean exists(ObjectMeta metadata) {
//    return this.get(metadata) != null;
//  }
}
