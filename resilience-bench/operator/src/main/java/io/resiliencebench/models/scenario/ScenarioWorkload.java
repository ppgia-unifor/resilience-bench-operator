package io.resiliencebench.models.scenario;

import java.util.Objects;

/**
 * Represents a workload in a scenario with a specified number of users.
 */
public class ScenarioWorkload {
  private String workloadName;
  private int users;

  /**
   * Default constructor for ScenarioWorkload.
   */
  public ScenarioWorkload() {
    // Default constructor for serialization/deserialization
  }

  /**
   * Constructs a ScenarioWorkload with the specified workload name and number of users.
   *
   * @param workloadName the name of the workload
   * @param users        the number of users
   */
  public ScenarioWorkload(String workloadName, int users) {
    this.workloadName = workloadName;
    this.users = users;
  }

  /**
   * Returns the name of the workload.
   *
   * @return the workload name
   */
  public String getWorkloadName() {
    return workloadName;
  }

  /**
   * Sets the name of the workload.
   *
   * @param workloadName the workload name to set
   */
  public void setWorkloadName(String workloadName) {
    this.workloadName = workloadName;
  }

  /**
   * Returns the number of users.
   *
   * @return the number of users
   */
  public int getUsers() {
    return users;
  }

  /**
   * Sets the number of users.
   *
   * @param users the number of users to set
   */
  public void setUsers(int users) {
    this.users = users;
  }

  /**
   * Returns a string representation of the ScenarioWorkload.
   *
   * @return a string representation of the ScenarioWorkload
   */
  @Override
  public String toString() {
    return String.format("%s-%d", workloadName, users);
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
    ScenarioWorkload that = (ScenarioWorkload) o;
    return users == that.users && Objects.equals(workloadName, that.workloadName);
  }

  /**
   * Returns a hash code value for the object.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(workloadName, users);
  }
}
