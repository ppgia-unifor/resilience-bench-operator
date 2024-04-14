package io.resiliencebench.execution.aws;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
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

  @Bean AmazonS3 createS3Client(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
  }

  @Bean AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }

  @Bean FileManager fileManager(AmazonS3 amazonS3, @Value("${storage.results.bucketName}") String bucketName) {
    try {
      if (amazonS3.doesBucketExistV2(bucketName)) {
        return new S3FileManager(amazonS3, bucketName);
      }
    } catch (Exception ex) {
      logger.warn("Unable to check it bucket " + bucketName + " exists. Using local file manager", ex);
    }
    return new LocalFileManager();
  }
}
