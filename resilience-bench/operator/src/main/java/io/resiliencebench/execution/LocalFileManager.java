package io.resiliencebench.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

public class LocalFileManager implements FileManager {
  private static final Logger log = LoggerFactory.getLogger(LocalFileManager.class);

  @Override
  public void save(String fileName, String destinationPath) {
    log.info("Saving file {} to {}", fileName, destinationPath);
  }
}
