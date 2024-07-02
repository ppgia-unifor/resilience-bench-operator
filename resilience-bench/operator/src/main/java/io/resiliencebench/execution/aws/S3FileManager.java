package io.resiliencebench.execution.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import io.resiliencebench.execution.localFileManager.model.FileManager;
import io.resiliencebench.models.enums.S3LogMessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of FileManager for AWS S3 file operations.
 */
public class S3FileManager implements FileManager {

  private static final Logger logger = LoggerFactory.getLogger(S3FileManager.class);

  private final AmazonS3 s3Client;
  private final String bucketName;
  private static final long PART_SIZE = 5 * 1024 * 1024; // part size set to 5 MB.

  /**
   * Constructs a new S3FileManager.
   *
   * @param s3Client   the Amazon S3 client to use
   * @param bucketName the name of the S3 bucket
   */
  public S3FileManager(final AmazonS3 s3Client, final String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  @Override
  public void save(final String fileName, final String destinationPath) {
    final File file = new File(fileName);
    if (!file.exists()) {
      logger.error(S3LogMessages.FILE_NOT_EXIST.format(fileName));
      return;
    }

    if (file.isDirectory()) {
      final File[] files = file.listFiles();
      if (files == null) {
        logger.error(S3LogMessages.FILE_DIRECTORY_LIST_FAILURE.format(fileName));
        return;
      }
      for (final File f : files) {
        internalSave(f, destinationPath);
      }
    } else {
      internalSave(file, destinationPath);
    }
  }

  @Override
  public Path resolveDestinationPath(final String fileName, final String destinationPath) {
    return Paths.get(destinationPath, new File(fileName).getName());
  }

  /**
   * Deletes a file from the S3 bucket.
   *
   * @param fileName the name of the file to delete
   */
  public void delete(final String fileName) {
    try {
      s3Client.deleteObject(bucketName, fileName);
      logger.info(S3LogMessages.FILE_DELETE_SUCCESS.format(fileName, bucketName));
    } catch (AmazonServiceException e) {
      logger.error(S3LogMessages.FILE_DELETE_FAILURE.format(fileName, bucketName, e.getErrorMessage()));
    } catch (SdkClientException e) {
      logger.error(S3LogMessages.S3_COMMUNICATION_FAILURE.format(e.getMessage()));
    }
  }

  /**
   * Lists files in the S3 bucket.
   *
   * @return a list of file names in the S3 bucket
   */
  public List<String> listFiles() {
    List<String> fileList = new ArrayList<>();
    try {
      ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
      ListObjectsV2Result result;
      do {
        result = s3Client.listObjectsV2(req);

        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
          fileList.add(objectSummary.getKey());
        }
        req.setContinuationToken(result.getNextContinuationToken());
      } while (result.isTruncated());

      logger.info(S3LogMessages.FILE_LIST_SUCCESS.format(bucketName));
    } catch (AmazonServiceException e) {
      logger.error(S3LogMessages.FILE_LIST_FAILURE.format(bucketName, e.getErrorMessage()));
    } catch (SdkClientException e) {
      logger.error(S3LogMessages.S3_COMMUNICATION_FAILURE.format(e.getMessage()));
    }
    return fileList;
  }

  private void internalSave(final File file, final String destinationPath) {
    if (!file.exists()) {
      logger.error(S3LogMessages.FILE_NOT_EXIST.format(file.getAbsolutePath()));
      return;
    }

    final String keyName = resolveDestinationPath(file.getName(), destinationPath).toString();
    final long contentLength = file.length();

    try {
      final List<PartETag> partETags = new ArrayList<>();
      final InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
      final InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

      long filePosition = 0;
      for (int i = 1; filePosition < contentLength; i++) {
        final long partSize = Math.min(PART_SIZE, (contentLength - filePosition));

        final UploadPartRequest uploadRequest = createUploadPartRequest(file, keyName, initResponse.getUploadId(), i, filePosition, partSize);
        final UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
        partETags.add(uploadResult.getPartETag());

        filePosition += partSize;
      }

      completeMultipartUpload(keyName, initResponse.getUploadId(), partETags);
      logger.info(S3LogMessages.FILE_UPLOAD_SUCCESS.format(file.getAbsolutePath(), keyName));
    } catch (AmazonServiceException e) {
      logger.error(S3LogMessages.S3_REQUEST_FAILURE.format(e.getRequestId(), e.getErrorMessage()));
    } catch (SdkClientException e) {
      logger.error(S3LogMessages.S3_COMMUNICATION_FAILURE.format(e.getMessage()));
    }
  }

  private UploadPartRequest createUploadPartRequest(File file, String keyName, String uploadId, int partNumber, long fileOffset, long partSize) {
    return new UploadPartRequest()
            .withBucketName(bucketName)
            .withKey(keyName)
            .withUploadId(uploadId)
            .withPartNumber(partNumber)
            .withFileOffset(fileOffset)
            .withFile(file)
            .withPartSize(partSize);
  }

  private void completeMultipartUpload(String keyName, String uploadId, List<PartETag> partETags) {
    CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);
    s3Client.completeMultipartUpload(compRequest);
  }
}
