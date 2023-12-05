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

public class CustomResourceRepository<T extends CustomResource> {

  private final KubernetesClient kubernetesClient;
  private final Class<T> resourceClass;
  private final MixedOperation<T, KubernetesResourceList<T>, Resource<T>> resources;

  @SuppressWarnings("unchecked")
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
    return inNamespace(resource).resource(resource).createOr(Replaceable::update);
  }

  public void delete(T resource) {
    inNamespace(resource).resource(resource).delete();
  }

  public T get(ObjectMeta meta) {
    return this.resources.inNamespace(meta.getNamespace()).withName(meta.getName()).get();
  }

  public List<T> list(String namespace) {
    return this.resources.inNamespace(namespace).list().getItems();
  }

//  public boolean exists(ObjectMeta metadata) {
//    return this.get(metadata) != null;
//  }
}
