package io.resiliencebench.execution.aws.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for AWS settings.
 */
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

	private String region;
	private String bucketName;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
}