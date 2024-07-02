package io.resiliencebench.execution.aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import io.resiliencebench.execution.localFileManager.model.FileManager;
import io.resiliencebench.execution.localFileManager.LocalFileManager;
import io.resiliencebench.execution.aws.properties.AwsProperties;
import io.resiliencebench.models.enums.AwsLogMessages;
import io.resiliencebench.models.enums.S3ErrorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration class for AWS services.
 */
@Configuration
public class AwsConfig {

  private static final Logger logger = LoggerFactory.getLogger(AwsConfig.class);

  private final AwsProperties awsProperties;

  public AwsConfig(AwsProperties awsProperties) {
    this.awsProperties = awsProperties;
  }

  /**
   * Creates an AmazonS3 client bean.
   *
   * @param credentialsProvider the AWS credentials provider
   * @return the AmazonS3 client
   */
  @Bean
  public AmazonS3 createS3Client(final AWSCredentialsProvider credentialsProvider) {
    return AmazonS3ClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(awsProperties.getRegion())
            .build();
  }

  /**
   * Creates an AWSCredentialsProvider bean.
   *
   * @return the AWS credentials provider
   */
  @Bean
  public AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }

  /**
   * Creates a FileManager bean. If the AWS bucket name is not set, it defaults to a local file manager.
   *
   * @param amazonS3 the AmazonS3 client
   * @return the FileManager instance
   */
  @Bean
  @Profile("!test")
  public FileManager fileManager(final AmazonS3 amazonS3) {
    return createFileManager(amazonS3, awsProperties.getBucketName());
  }

  /**
   * Creates a FileManager instance based on the provided parameters.
   *
   * @param amazonS3 the AmazonS3 client
   * @param bucketName the name of the S3 bucket
   * @return a FileManager instance
   */
  private FileManager createFileManager(final AmazonS3 amazonS3, final String bucketName) {
    if (bucketName == null || bucketName.isEmpty()) {
      logger.info(AwsLogMessages.LOCAL_FILE_MANAGER.getMessage());
      return new LocalFileManager();
    }

    try {
      logger.info(AwsLogMessages.S3_CONNECTION_ATTEMPT.getMessage(), bucketName, awsProperties.getRegion());
      amazonS3.headBucket(new HeadBucketRequest(bucketName));
      logger.info(AwsLogMessages.S3_CONNECTION_SUCCESS.getMessage(), bucketName);
      return new S3FileManager(amazonS3, bucketName);
    } catch (AmazonS3Exception e) {
      handleS3Exception(bucketName, e);
      return new LocalFileManager();
    } catch (SdkClientException ex) {
      logger.error(AwsLogMessages.S3_COMMUNICATION_FAILURE.getMessage(), bucketName, ex.getMessage());
      return new LocalFileManager();
    }
  }

  /**
   * Handles the AmazonS3Exception by logging appropriate messages based on the error type.
   *
   * @param bucketName the name of the S3 bucket
   * @param e the AmazonS3Exception
   */
  private void handleS3Exception(final String bucketName, final AmazonS3Exception e) {
    S3ErrorType errorType = S3ErrorType.fromStatusCode(e.getStatusCode());
    switch (errorType) {
      case FORBIDDEN:
        logger.error(AwsLogMessages.S3_ACCESS_FORBIDDEN.getMessage(), bucketName);
        break;
      case NOT_FOUND:
        logger.error(AwsLogMessages.S3_BUCKET_NOT_FOUND.getMessage(), bucketName);
        break;
      case UNKNOWN_ERROR:
      default:
        logger.error(AwsLogMessages.S3_CONNECTION_ERROR.getMessage(), bucketName, e.getErrorMessage());
        break;
    }
  }
}
