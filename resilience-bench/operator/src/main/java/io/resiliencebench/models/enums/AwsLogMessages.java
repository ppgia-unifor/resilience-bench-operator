package io.resiliencebench.models.enums;

/**
 * Enum representing log messages for AWS operations.
 */
public enum AwsLogMessages {
	S3_CONNECTION_SUCCESS("Successfully connected to S3 bucket {}"),
	S3_CONNECTION_ATTEMPT("Attempting to connect to S3 bucket {} in region {}"),
	S3_ACCESS_FORBIDDEN("Access to the S3 bucket {} is forbidden. Using local file manager instead."),
	S3_BUCKET_NOT_FOUND("The S3 bucket {} does not exist. Using local file manager instead."),
	S3_CONNECTION_ERROR("Failed to connect to S3 bucket {}. Error message: {}. Using local file manager instead."),
	LOCAL_FILE_MANAGER("AWS_BUCKET_NAME is not set. Using local file manager."),
	S3_COMMUNICATION_FAILURE("Failed to communicate with S3 for bucket {}. Error message: {}. Using local file manager instead.");

	private final String message;

	AwsLogMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
