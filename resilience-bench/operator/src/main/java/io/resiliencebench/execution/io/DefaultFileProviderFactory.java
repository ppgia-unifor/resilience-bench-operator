package io.resiliencebench.execution.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static java.util.Optional.*;

@Component
public class DefaultFileProviderFactory implements FileProviderFactory {

  private final static Logger logger = LoggerFactory.getLogger(DefaultFileProviderFactory.class);

  private final S3FileProvider s3FileProvider;
  private final LocalFileProvider localFileProvider;
  private final Environment env;

  public DefaultFileProviderFactory(S3FileProvider s3FileProvider,
                                    LocalFileProvider localFileProvider,
                                    Environment env
  ) {
    this.s3FileProvider = s3FileProvider;
    this.localFileProvider = localFileProvider;
    this.env = env;
  }

  @Override
  public FileProvider create() {
    var storageType = ofNullable(env.getProperty("STORAGE_TYPE"));
    if (storageType.isPresent()) {
      if ("CLOUD".equalsIgnoreCase(storageType.get())) {
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
