package io.resiliencebench.models.service;

import io.fabric8.kubernetes.api.model.LabelSelector;

import java.util.Objects;

/**
 * Represents the specification for a ResilientService.
 */
public class ResilientServiceSpec {

  private LabelSelector selector;
  private String host;
  private String subset;

  /**
   * Returns the label selector for the ResilientService.
   *
   * @return the label selector
   */
  public LabelSelector getSelector() {
    return selector;
  }

  /**
   * Sets the label selector for the ResilientService.
   *
   * @param selector the label selector to set
   */
  public void setSelector(LabelSelector selector) {
    this.selector = selector;
  }

  /**
   * Returns the host for the ResilientService.
   *
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * Sets the host for the ResilientService.
   *
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * Returns the subset for the ResilientService.
   *
   * @return the subset
   */
  public String getSubset() {
    return subset;
  }

  /**
   * Sets the subset for the ResilientService.
   *
   * @param subset the subset to set
   */
  public void setSubset(String subset) {
    this.subset = subset;
  }

  /**
   * Returns a string representation of the ResilientServiceSpec.
   *
   * @return a string representation of the ResilientServiceSpec
   */
  @Override
  public String toString() {
    return "ResilientServiceSpec{" +
            "selector=" + selector +
            ", host='" + host + '\'' +
            ", subset='" + subset + '\'' +
            '}';
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o the reference object with which to compare
   * @return true if this object is the same as the obj argument; false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResilientServiceSpec that = (ResilientServiceSpec) o;
    return Objects.equals(selector, that.selector) &&
            Objects.equals(host, that.host) &&
            Objects.equals(subset, that.subset);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(selector, host, subset);
  }
}
