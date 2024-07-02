package io.resiliencebench.execution;

import io.fabric8.kubernetes.api.model.HasMetadata;
import java.util.List;

/**
 * Interface representing a chain of executor steps.
 *
 * @param <T> the type of the execution context or result that each step operates on,
 *            which must implement {@link HasMetadata}
 */
public interface ExecutorStepChain<T extends HasMetadata> {

  /**
   * Gets the preparation steps to be executed before the main execution.
   *
   * @return an unmodifiable list of preparation steps
   */
  List<ExecutorStep<T>> getPreparationSteps();

  /**
   * Gets the post-execution steps to be executed after the main execution.
   *
   * @return an unmodifiable list of post-execution steps
   */
  List<ExecutorStep<T>> getPostExecutionSteps();

  /**
   * Adds a preparation step to the chain.
   *
   * @param step the preparation step to add
   */
  void addPreparationStep(ExecutorStep<T> step);

  /**
   * Removes a preparation step from the chain.
   *
   * @param step the preparation step to remove
   */
  void removePreparationStep(ExecutorStep<T> step);

  /**
   * Adds a post-execution step to the chain.
   *
   * @param step the post-execution step to add
   */
  void addPostExecutionStep(ExecutorStep<T> step);

  /**
   * Removes a post-execution step from the chain.
   *
   * @param step the post-execution step to remove
   */
  void removePostExecutionStep(ExecutorStep<T> step);
}
