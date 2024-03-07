package io.resiliencebench.execution.aws;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.resiliencebench.execution.FileManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

  @Bean AmazonS3 createS3Client(AWSCredentialsProvider credentialsProvider) {
    return AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(Regions.US_EAST_1).build();
  }

  @Bean AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }

  @Bean FileManager fileManager(AmazonS3 amazonS3) {
    return new S3FileManager(amazonS3);
  }
}
