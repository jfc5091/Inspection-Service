package com.firerms.amazon.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.firerms.multiTenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

@Component(value="ClientService")
public class AmazonS3ClientService implements AmazonS3ClientServiceInterface {

    private final String awsS3Bucket;
    private final AmazonS3 amazonS3;

    private final String DEFAULT_FOLDER = "inspections/";
    private final TimeZone tz = TimeZone.getTimeZone("UTC");
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm'Z'");

    private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ClientService.class);

    @Autowired
    public AmazonS3ClientService(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider, String awsS3Bucket)
    {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion.getName()).build();
        this.awsS3Bucket = awsS3Bucket;
    }

    @Async
    public String uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        assert fileName != null;

        df.setTimeZone(tz);
        String isoDate = df.format(new Date());

        if (fileName.isEmpty()) {
            Random random = new Random();
            int randomInt = random.nextInt(99999);
            fileName = "untitled" + randomInt + ".";
        }
        if (!fileName.contains(".")) {
            fileName = fileName + ".";
        }

        String uniqueFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "-" +
                TenantContext.getCurrentTenant() + "-" +
                isoDate;
        File file = new File(uniqueFileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(awsS3Bucket, DEFAULT_FOLDER + uniqueFileName, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            amazonS3.putObject(putObjectRequest);
            return amazonS3.getUrl(awsS3Bucket, DEFAULT_FOLDER + uniqueFileName).toString();
        } catch (IOException | AssertionError | AmazonServiceException ex) {
            LOG.error("Inspection Service - error [" + ex.getMessage() + "] occurred while uploading [" + uniqueFileName + "] ");
            throw ex;
        } finally {
            if (!file.delete()) {
                LOG.warn("Inspection Service - warning failed to delete local file " + uniqueFileName);
            }
        }
    }

    @Async
    public void deleteFileFromS3Bucket(String url) {
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(awsS3Bucket, DEFAULT_FOLDER + fileName));
        } catch (AmazonServiceException ex) {
            LOG.error("Inspection Service - error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
            throw ex;
        }
    }
}
