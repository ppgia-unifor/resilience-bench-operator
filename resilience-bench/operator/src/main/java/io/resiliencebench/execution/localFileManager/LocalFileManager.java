package io.resiliencebench.execution.localFileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import io.resiliencebench.execution.localFileManager.model.FileManager;

/**
 * Implementation of FileManager for local file system operations.
 */
public class LocalFileManager implements FileManager {

  private static final Logger logger = LoggerFactory.getLogger(LocalFileManager.class);

  /**
   * Saves a file to the specified destination path.
   *
   * @param fileName        the name of the file to save
   * @param destinationPath the path to save the file to
   * @throws IllegalArgumentException if fileName or destinationPath are invalid
   * @throws IOException if an I/O error occurs
   */
  @Override
  public void save(final String fileName, final String destinationPath) throws IllegalArgumentException, IOException {
    validateParameters(fileName, destinationPath);

    final File file = new File(fileName);
    if (!file.exists()) {
      final String errorMessage = String.format("File %s does not exist", fileName);
      logger.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }

    final Path destination = resolveDestinationPath(fileName, destinationPath);

    try {
      Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
      logger.info("File {} successfully saved to {}", fileName, destinationPath);
    } catch (IOException e) {
      logger.error("Failed to save file {} to {}. {}", fileName, destinationPath, e.getMessage());
      throw e;
    }
  }

  /**
   * Resolves the destination path for the file to be saved.
   *
   * @param fileName        the name of the file to save
   * @param destinationPath the path to save the file to
   * @return the resolved destination path
   */
  @Override
  public Path resolveDestinationPath(final String fileName, final String destinationPath) {
    return Paths.get(destinationPath, new File(fileName).getName());
  }

  /**
   * Validates the input parameters.
   *
   * @param fileName        the name of the file to save
   * @param destinationPath the path to save the file to
   * @throws IllegalArgumentException if fileName or destinationPath are invalid
   */
  private void validateParameters(final String fileName, final String destinationPath) {
    if (fileName == null || fileName.isEmpty()) {
      final String errorMessage = "File name must not be null or empty";
      logger.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }

    if (destinationPath == null || destinationPath.isEmpty()) {
      final String errorMessage = "Destination path must not be null or empty";
      logger.error(errorMessage);
      throw new IllegalArgumentException(errorMessage);
    }
  }
}
