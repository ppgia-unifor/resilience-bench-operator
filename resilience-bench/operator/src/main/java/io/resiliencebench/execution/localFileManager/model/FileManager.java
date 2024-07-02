package io.resiliencebench.execution.steps.model;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for file management operations.
 */
public interface FileManager {

  /**
   * Saves a file to the specified destination path.
   *
   * @param fileName        the name of the file to save
   * @param destinationPath the path to save the file to
   * @throws IllegalArgumentException if fileName or destinationPath are invalid
   * @throws IOException if an I/O error occurs
   */
  void save(String fileName, String destinationPath) throws IllegalArgumentException, IOException;

  /**
   * Resolves the destination path for the file to be saved.
   *
   * @param fileName        the name of the file to save
   * @param destinationPath the path to save the file to
   * @return the resolved destination path
   */
  Path resolveDestinationPath(String fileName, String destinationPath);
}
