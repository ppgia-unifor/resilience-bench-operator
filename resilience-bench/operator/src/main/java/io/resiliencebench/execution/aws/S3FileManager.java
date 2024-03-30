package io.resiliencebench.execution.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import io.resiliencebench.execution.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class S3FileManager implements FileManager {
  private final Logger logger = LoggerFactory.getLogger(S3FileManager.class);
  private final AmazonS3 s3Client;
  private final String bucketName = System.getenv("AWS_BUCKET_NAME");
  private final static long PART_SIZE = 5 * 1024 * 1024; // part size to 5 MB.

  public S3FileManager(AmazonS3 s3Client) {
    this.s3Client = s3Client;
  }

  @Override
  public void save(String file, String destinationPath) {
    var keyName = Paths.get(destinationPath, file).toAbsolutePath().toString();
    var contentLength = file.length();

    try {
      var partETags = new ArrayList<PartETag>();
      var initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
      var initResponse = s3Client.initiateMultipartUpload(initRequest);

      long filePosition = 0;
      for (int i = 1; filePosition < contentLength; i++) {
        var partSize = Math.min(PART_SIZE, (contentLength - filePosition));

        var uploadRequest = new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(keyName)
                .withUploadId(initResponse.getUploadId())
                .withPartNumber(i)
                .withFileOffset(filePosition)
                .withFile(new File(file))
                .withPartSize(partSize);

        var uploadResult = s3Client.uploadPart(uploadRequest);
        partETags.add(uploadResult.getPartETag());

        filePosition += partSize;
      }

      var compRequest = new CompleteMultipartUploadRequest(bucketName, keyName, initResponse.getUploadId(), partETags);
      s3Client.completeMultipartUpload(compRequest);
      logger.info("File {} successfully uploaded", keyName);
    }
    catch(AmazonServiceException e) {
      logger.error("The call was transmitted successfully with requestId {}, but Amazon S3 couldn't process it. Message {}", e.getRequestId(), e.getErrorMessage());
      throw e;
    }
    catch(SdkClientException e) {
      logger.error("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3. {}", e.getMessage());
      throw e;
    }
  }
}