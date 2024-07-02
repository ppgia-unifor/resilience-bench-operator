package io.resiliencebench.models.enums;

/**
 * Enum representing different types of S3 errors.
 */
public enum S3ErrorType {
	FORBIDDEN,
	NOT_FOUND,
	UNKNOWN_ERROR;

	/**
	 * Converts an HTTP status code to an S3ErrorType.
	 *
	 * @param statusCode the HTTP status code
	 * @return the corresponding S3ErrorType
	 */
	public static S3ErrorType fromStatusCode(int statusCode) {
		switch (statusCode) {
			case 403:
				return FORBIDDEN;
			case 404:
				return NOT_FOUND;
			default:
				return UNKNOWN_ERROR;
		}
	}
}
