package io.resiliencebench.support;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.ResourceNotFoundException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.resiliencebench.models.enums.CustomResourceRepositoryMessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static io.javaoperatorsdk.operator.processing.KubernetesResourceUtils.getName;

/**
 * Repository for managing custom Kubernetes resources.
 *
 * @param <T> the type of the custom resource
 */
public class CustomResourceRepository<T extends CustomResource & Namespaced> {

  private static final Logger logger = LoggerFactory.getLogger(CustomResourceRepository.class);

  private final MixedOperation<T, KubernetesResourceList<T>, Resource<T>> resourceOperation;

  /**
   * Constructs a new CustomResourceRepository.
   *
   * @param kubernetesClient the Kubernetes client
   * @param resourceClass    the class of the custom resource
   */
  public CustomResourceRepository(KubernetesClient kubernetesClient, Class<T> resourceClass) {
    this(kubernetesClient.resources(resourceClass));
  }

  /**
   * Constructs a new CustomResourceRepository.
   *
   * @param resourceOperation the resource operation
   */
  public CustomResourceRepository(MixedOperation<T, KubernetesResourceList<T>, Resource<T>> resourceOperation) {
    this.resourceOperation = resourceOperation;
  }

  private NonNamespaceOperation<T, KubernetesResourceList<T>, Resource<T>> inNamespace(T resource) {
    String namespace = resource.getMetadata().getNamespace();
    if (namespace == null || namespace.isEmpty()) {
      throw new IllegalArgumentException("Namespace must not be null or empty");
    }
    return this.resourceOperation.inNamespace(namespace);
  }

  /**
   * Creates a new custom resource.
   *
   * @param resource the custom resource to create
   * @return the created custom resource
   */
  public T create(T resource) {
    logger.debug(CustomResourceRepositoryMessages.CREATING_RESOURCE.format(getName(resource)));
    return inNamespace(resource).resource(resource).create();
  }

  /**
   * Updates an existing custom resource.
   *
   * @param resource the custom resource to update
   * @return the updated custom resource
   */
  public T update(T resource) {
    logger.debug(CustomResourceRepositoryMessages.UPDATING_RESOURCE.format(getName(resource), resource.getMetadata().getResourceVersion()));
    return resource(resource).update();
  }

  /**
   * Updates the status of a custom resource.
   *
   * @param resource the custom resource to update
   * @return the updated custom resource with the new status
   */
  public T updateStatus(T resource) {
    logger.trace(CustomResourceRepositoryMessages.UPDATING_STATUS.format(getName(resource)));
    return resource(resource).lockResourceVersion().updateStatus();
  }

  /**
   * Patches the status of a custom resource.
   *
   * @param resource         the custom resource with the new status
   * @param originalResource the original custom resource
   * @return the patched custom resource with the new status
   */
  public T patchStatus(T resource, T originalResource) {
    logger.trace(CustomResourceRepositoryMessages.PATCHING_STATUS.format(getName(resource)));
    String resourceVersion = resource.getMetadata().getResourceVersion();
    originalResource.getMetadata().setResourceVersion(null);
    resource.getMetadata().setResourceVersion(null);
    try {
      return resource(originalResource).editStatus(r -> resource);
    } finally {
      originalResource.getMetadata().setResourceVersion(resourceVersion);
      resource.getMetadata().setResourceVersion(resourceVersion);
    }
  }

  /**
   * Deletes all custom resources in the specified namespace.
   *
   * @param namespace the namespace to delete resources from
   */
  public void deleteAll(String namespace) {
    if (namespace == null || namespace.isEmpty()) {
      throw new IllegalArgumentException("Namespace must not be null or empty");
    }
    resourceOperation.inNamespace(namespace).list().getItems().forEach(r -> {
      logger.debug(CustomResourceRepositoryMessages.DELETING_RESOURCE.format(getName(r)));
      inNamespace(r).delete();
    });
  }

  /**
   * Finds a custom resource by its metadata.
   *
   * @param meta the metadata of the custom resource
   * @return an Optional containing the found custom resource, or empty if not found
   */
  public Optional<T> find(ObjectMeta meta) {
    if (meta == null) {
      throw new IllegalArgumentException("Metadata must not be null");
    }
    return find(meta.getNamespace(), meta.getName());
  }

  /**
   * Gets a custom resource by its namespace and name.
   *
   * @param namespace the namespace of the custom resource
   * @param name      the name of the custom resource
   * @return the found custom resource
   * @throws ResourceNotFoundException if the custom resource is not found
   */
  public T get(String namespace, String name) {
    return find(namespace, name).orElseThrow(() ->
            new ResourceNotFoundException(CustomResourceRepositoryMessages.RESOURCE_NOT_FOUND.format(namespace, name))
    );
  }

  /**
   * Finds a custom resource by its namespace and name.
   *
   * @param namespace the namespace of the custom resource
   * @param name      the name of the custom resource
   * @return an Optional containing the found custom resource, or empty if not found
   */
  public Optional<T> find(String namespace, String name) {
    if (namespace != null) {
      return Optional.ofNullable(resourceOperation.inNamespace(namespace).withName(name).get());
    } else {
      return Optional.ofNullable(resourceOperation.withName(name).get());
    }
  }

  /**
   * Lists all custom resources in the specified namespace.
   *
   * @param namespace the namespace to list resources from
   * @return a list of custom resources in the specified namespace
   */
  public List<T> list(String namespace) {
    if (namespace == null || namespace.isEmpty()) {
      throw new IllegalArgumentException("Namespace must not be null or empty");
    }
    return resourceOperation.inNamespace(namespace).list().getItems();
  }

  private Resource<T> resource(T resource) {
    return resource != null ?
            resourceOperation.inNamespace(resource.getMetadata().getNamespace()).resource(resource) :
            resourceOperation.resource(resource);
  }
}
