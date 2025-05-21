package io.resiliencebench.execution.io;

import java.util.Optional;

public interface FileProvider {

  void writeToFile(String resultFile, String content);

  Optional<String> getFileAsString(String resultFile);
}
