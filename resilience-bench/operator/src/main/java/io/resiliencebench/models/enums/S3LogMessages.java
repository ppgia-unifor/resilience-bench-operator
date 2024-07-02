package io.resiliencebench.models.enums;

/**
 * Enum representing log messages for file operations.
 */
public enum S3LogMessages {
	FILE_NOT_EXIST("File %s does not exist"),
	DIRECTORY_NOT_EXIST("Directory %s does not exist"),
	FILE_UPLOAD_SUCCESS("File %s successfully uploaded to %s"),
	FILE_DELETE_SUCCESS("File %s successfully deleted from bucket %s"),
	FILE_LIST_SUCCESS("Successfully listed files in bucket %s"),
	FILE_LIST_FAILURE("Failed to list files in bucket %s. Error message: %s"),
	FILE_DELETE_FAILURE("Failed to delete file %s from bucket %s. Error message: %s"),
	FILE_SAVE_FAILURE("Failed to save file %s to %s. Error message: %s"),
	FILE_DIRECTORY_LIST_FAILURE("Failed to list files in directory %s"),
	S3_COMMUNICATION_FAILURE("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3. Error message: %s"),
	S3_REQUEST_FAILURE("The call was transmitted successfully with requestId %s, but Amazon S3 couldn't process it. Error message: %s");

	private final String message;

	S3LogMessages(String message) {
		this.message = message;
	}

	public String format(Object... args) {
		return String.format(message, args);
	}
}
