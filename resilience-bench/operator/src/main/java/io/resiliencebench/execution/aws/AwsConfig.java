package io.resiliencebench.execution.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.resiliencebench.execution.FileManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

  @Value("${cloud.aws.region.static}")
  private String region;

  @Bean AmazonS3 createS3Client(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
  }

  @Bean AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }

  @Bean FileManager fileManager(AmazonS3 amazonS3, @Value("${storage.results.bucketName}") String bucketName) {
    if (amazonS3.doesBucketExistV2(bucketName)) {
      return new S3FileManager(amazonS3, bucketName);
    }
    throw new IllegalArgumentException("Bucket " + bucketName + " does not exist");
  }
}
