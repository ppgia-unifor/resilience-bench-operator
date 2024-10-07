package io.resiliencebench.execution.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.lang.System.*;
import static java.util.Optional.*;

@Component
public class DefaultFileProviderFactory implements FileProviderFactory {

  private final static Logger logger = LoggerFactory.getLogger(DefaultFileProviderFactory.class);

  private final S3FileProvider s3FileProvider;
  private final LocalFileProvider localFileProvider;

  public DefaultFileProviderFactory(S3FileProvider s3FileProvider, LocalFileProvider localFileProvider) {
    this.s3FileProvider = s3FileProvider;
    this.localFileProvider = localFileProvider;
  }

  @Override
  public FileProvider create() {
    var storageType = ofNullable(getenv("STORAGE"));
    if (storageType.isPresent()) {
      if ("CLOUD".equals(storageType.get())) {
        return s3FileProvider;
      } else {
        return localFileProvider;
      }
    } else {
      logger.info("No storage type specified, using local storage");
      return localFileProvider;
    }
  }
}
