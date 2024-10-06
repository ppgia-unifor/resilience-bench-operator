package io.resiliencebench.execution.io;

import org.springframework.stereotype.Component;

@Component
public class FileProviderFactory {

  private final S3FileProvider s3FileProvider;
  private final LocalFileProvider localFileProvider;

  public FileProviderFactory(S3FileProvider s3FileProvider, LocalFileProvider localFileProvider) {
    this.s3FileProvider = s3FileProvider;
    this.localFileProvider = localFileProvider;
  }

  public FileProvider getFileProvider(boolean useCloud) {
    if (useCloud) {
      return s3FileProvider;
    } else {
      return localFileProvider;
    }
  }
}
