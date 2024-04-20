package io.resiliencebench.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

public class LocalFileManager implements FileManager {
  private static final Logger log = LoggerFactory.getLogger(LocalFileManager.class);

  @Override
  public void save(String fileName, String destinationPath) {
    Path tempDirectory = null;
    try {
      tempDirectory = Files.createTempDirectory(destinationPath);
      var sourcePath = Paths.get(fileName);
      Files.copy(sourcePath, tempDirectory, StandardCopyOption.REPLACE_EXISTING);
      log.info("File {} successfully created", Paths.get(tempDirectory.toString(), sourcePath.getFileName().toString()));
    } catch (IOException e) {
      if (tempDirectory == null) {
        log.error("Failed to create temporary directory");
      } else {
        log.error("Failed to copy file from " + fileName + " to " + tempDirectory);
      }
    }
  }
}
