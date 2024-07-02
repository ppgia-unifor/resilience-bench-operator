package io.resiliencebench.support;

import java.util.Objects;

/**
 * Represents a reference to a ConfigMap object in Kubernetes.
 */
public class ConfigMapReference {

  private String name;
  private String file;

  public ConfigMapReference() {
    // Default constructor needed for Jackson deserialization
  }

  public ConfigMapReference(String name, String file) {
    this.name = name;
    this.file = file;
  }

  /**
   * Returns the name of the ConfigMap.
   *
   * @return the name of the ConfigMap
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the ConfigMap.
   *
   * @param name the name of the ConfigMap
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the file within the ConfigMap.
   *
   * @return the file within the ConfigMap
   */
  public String getFile() {
    return file;
  }

  /**
   * Sets the file within the ConfigMap.
   *
   * @param file the file within the ConfigMap
   */
  public void setFile(String file) {
    this.file = file;
  }

  /**
   * Provides a string representation of the ConfigMapReference.
   *
   * @return a string representation of the ConfigMapReference
   */
  @Override
  public String toString() {
    return "ConfigMapReference{" +
            "name='" + name + '\'' +
            ", file='" + file + '\'' +
            '}';
  }

  /**
   * Checks whether this ConfigMapReference is equal to another object.
   *
   * @param o the object to compare to
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfigMapReference that = (ConfigMapReference) o;
    return Objects.equals(name, that.name) && Objects.equals(file, that.file);
  }

  /**
   * Generates a hash code for this ConfigMapReference.
   *
   * @return an integer hash code value
   */
  @Override
  public int hashCode() {
    return Objects.hash(name, file);
  }
}
