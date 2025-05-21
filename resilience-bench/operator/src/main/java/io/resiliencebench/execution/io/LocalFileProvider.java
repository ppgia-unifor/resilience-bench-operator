package io.resiliencebench.execution.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Component("localFileProvider")
public class LocalFileProvider implements FileProvider {

  private final static Logger logger = LoggerFactory.getLogger(LocalFileProvider.class);

  @Override
  public void writeToFile(String resultFile, String content) {
    try (var outputStream = new FileOutputStream(resultFile)) {
      outputStream.write(content.getBytes());
    } catch (IOException e) {
      logger.warn("Error writing file {}. {}", resultFile, e.getMessage());
    }
  }

  @Override
  public Optional<String> getFileAsString(String resultFile) {
    try (var inputStream = new FileInputStream(resultFile)) {
      return of(new String(inputStream.readAllBytes()));
    } catch (IOException e) {
      logger.warn("Error reading file {}. {}", resultFile, e.getMessage());
      return empty();
    }
  }
}
