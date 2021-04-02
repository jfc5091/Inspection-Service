package com.firerms.amazon.s3;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AmazonS3ClientServiceInterface {

    String uploadFileToS3Bucket(MultipartFile multipartFile, boolean enablePublicReadAccess) throws IOException;

    void deleteFileFromS3Bucket(String fileName);
}
