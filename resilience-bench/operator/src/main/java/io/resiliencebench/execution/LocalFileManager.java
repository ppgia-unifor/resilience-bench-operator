package io.resiliencebench.execution;

import java.io.IOException;
import java.nio.file.*;

public class LocalFileManager implements FileManager {
  @Override
  public void save(String fileName, String destinationPath) {
    try {
      Path sourcePath = Paths.get(fileName);
      Path destination = Paths.get(destinationPath);

      // Ensure the parent directories exist
      Files.createDirectories(destination.getParent());

      // Copy the file to the new location, replacing the existing file if it already exists
      Files.copy(sourcePath, destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to copy file from " + fileName + " to " + destinationPath, e);
    }
  }
}
