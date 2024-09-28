package io.resiliencebench.execution.steps.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.resiliencebench.execution.steps.ExecutorStep;
import io.resiliencebench.resources.queue.ExecutionQueue;
import io.resiliencebench.resources.scenario.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AwsResultFileStep extends ExecutorStep {

  private final Logger log = LoggerFactory.getLogger(AwsResultFileStep.class);
  private final AmazonS3 s3Client;

  private final String bucketName;
  private final static long PART_SIZE = 5 * 1024 * 1024; // part size to 5 MB.

  public AwsResultFileStep(KubernetesClient kubernetesClient, AmazonS3 s3Client, String bucketName) {
    super(kubernetesClient);
      this.s3Client = s3Client;
      this.bucketName = bucketName;
  }

  @Override
  protected boolean isApplicable(Scenario scenario) {
    return false;
  }

  @Override
  protected void internalExecute(Scenario scenario, ExecutionQueue queue) {
  }

  public void save(String fileName, String destinationPath) {
    var files = new File(fileName).listFiles();
    if (files == null) {
      log.error("Directory {} does not exist", fileName);
      return;
    }
    for (var file : files) {
      internalSave(file.getAbsolutePath(), destinationPath);
    }
  }

  private void internalSave(String fileName, String destinationPath) {
    var file = new File(fileName);
    if (!file.exists()) {
      log.error("File {} does not exist", fileName);
      return;
    }
    var keyName = Paths.get(destinationPath, file.getName()).toString();
    var contentLength = file.length();

    try {
      var partETags = new ArrayList<PartETag>();
      var initRequest = new InitiateMultipartUploadRequest(bucketName, keyName);
      var initResponse = s3Client.initiateMultipartUpload(initRequest);

      long filePosition = 0;
      for (var i = 1; filePosition < contentLength; i++) {
        var partSize = Math.min(PART_SIZE, (contentLength - filePosition));

        var uploadRequest = new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(keyName)
                .withUploadId(initResponse.getUploadId())
                .withPartNumber(i)
                .withFileOffset(filePosition)
                .withFile(file)
                .withPartSize(partSize);

        var uploadResult = s3Client.uploadPart(uploadRequest);
        partETags.add(uploadResult.getPartETag());

        filePosition += partSize;
      }

      var compRequest = new CompleteMultipartUploadRequest(bucketName, keyName, initResponse.getUploadId(), partETags);
      s3Client.completeMultipartUpload(compRequest);
      log.info("File {} successfully uploaded", keyName);
    }
    catch(AmazonServiceException e) {
      log.error("The call was transmitted successfully with requestId {}, but Amazon S3 couldn't process it. Message {}", e.getRequestId(), e.getErrorMessage());
    }
    catch(SdkClientException e) {
      log.error("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3. {}", e.getMessage());
    }
  }
}
