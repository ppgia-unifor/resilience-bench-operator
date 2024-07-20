package io.resiliencebench.execution.steps.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.HeadBucketRequest;

import io.resiliencebench.execution.FileManager;
import io.resiliencebench.execution.LocalFileManager;

@Configuration
public class AwsConfig {

  private static final Logger logger = LoggerFactory.getLogger(AwsConfig.class);

  @Value("${AWS_REGION:us-east-1}")
  private String region;

  @Value("${AWS_BUCKET_NAME:none}")
  private String bucketName;

  @Bean AmazonS3 createS3Client(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
  }

  @Bean AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
}

  @Bean FileManager fileManager(AmazonS3 amazonS3) {
    try {
      logger.info("Attempting to connect to S3 bucket " + bucketName + " in region " + region);
      amazonS3.headBucket(new HeadBucketRequest(bucketName));
      logger.info("Successfully connected to S3 bucket " + bucketName);
      return new S3FileManager(amazonS3, bucketName);
    } catch (SdkClientException ex) {
      logger.error("Failed to connect to S3 bucket " + bucketName + ex.getMessage() + ". Using local file manager instead");
      return new LocalFileManager();
    }
  }
}
