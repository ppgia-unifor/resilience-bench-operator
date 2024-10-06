package io.resiliencebench.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

  AmazonS3 createS3Client(@Value("${AWS_REGION:us-east-1}") String region, AWSCredentialsProvider credentialsProvider) {
    return AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
  }

  AWSCredentialsProvider credentialsProvider() {
    return new DefaultAWSCredentialsProviderChain();
  }
}
