package io.resiliencebench.execution.aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import io.resiliencebench.execution.FileManager;
import io.resiliencebench.execution.LocalFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

  private static final Logger logger = LoggerFactory.getLogger(AwsConfig.class);

  @Value("${cloud.aws.region.static}")
  private String region;

  @Value("${storage.results.bucketName}")
  private String bucketName;

  @Bean AmazonS3 createS3Client(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
  }

  @Bean AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }

  @Bean FileManager fileManager(AmazonS3 amazonS3) {
    try {
      amazonS3.headBucket(new HeadBucketRequest(bucketName));
      return new S3FileManager(amazonS3, bucketName);
    } catch (SdkClientException ex) {
      logger.error("Bucket " + bucketName + " does not exists. Using local file manager instead", ex);
      return new LocalFileManager();
    }
  }
}
