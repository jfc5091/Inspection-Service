package com.firerms.amazon.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.firerms.InspectionServiceApplication;
import com.firerms.multiTenancy.TenantContext;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest(classes= InspectionServiceApplication.class)
public class AmazonS3ClientServiceUnitTests {

    @Autowired
    AmazonS3ClientService amazonS3ClientService;

    @MockBean
    AmazonS3 amazonS3;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant("1");
    }

    @Test
    void uploadFileToS3BucketTest() throws IOException, IllegalAccessException {
        String originalFileName = "inspection-violation-test-image";
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", originalFileName + ".png",
                "image/png", fis);
        String testBucket = "testBucket";
        String urlString = "http://" + originalFileName + ".com";

        FieldUtils.writeDeclaredField(amazonS3ClientService, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientService, "awsS3Bucket", testBucket, true);

        URL url = new URL(urlString);

        when(amazonS3.getUrl(eq(testBucket), anyString())).thenReturn(url);

        String imageUrl = (amazonS3ClientService.uploadFileToS3Bucket(image, true));

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(eq(testBucket), Mockito.contains(originalFileName));
        assertEquals(urlString, imageUrl);
    }

    @Test
    void uploadFileToS3BucketDisablePublicReadAccessTest() throws IOException, IllegalAccessException {
        String originalFileName = "inspection-violation-test-image";
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", originalFileName + ".png",
                "image/png", fis);
        String testBucket = "testBucket";
        String urlString = "http://" + originalFileName + ".com";

        FieldUtils.writeDeclaredField(amazonS3ClientService, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientService, "awsS3Bucket", testBucket, true);

        URL url = new URL(urlString);

        when(amazonS3.getUrl(eq(testBucket), anyString())).thenReturn(url);

        String imageUrl = (amazonS3ClientService.uploadFileToS3Bucket(image, false));

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(eq(testBucket), Mockito.contains(originalFileName));
        assertEquals(urlString, imageUrl);
    }

    @Test
    void uploadFileToS3BucketFilenameIsEmptyTest() throws IOException, IllegalAccessException {
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", "",
                "image/png", fis);
        String testBucket = "testBucket";
        String newFileName = "untitled";
        String urlString = "http://" + newFileName + ".com";

        FieldUtils.writeDeclaredField(amazonS3ClientService, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientService, "awsS3Bucket", testBucket, true);

        URL url = new URL(urlString);

        when(amazonS3.getUrl(eq(testBucket), any())).thenReturn(url);

        String imageUrl = (amazonS3ClientService.uploadFileToS3Bucket(image, true));

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(eq(testBucket), Mockito.contains(newFileName));
        assertEquals(urlString, imageUrl);
    }

    @Test
    void uploadFileToS3BucketDoesNotHaveFileTypeTest() throws IOException, IllegalAccessException {
        String fileName = "ambiguous";
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", fileName,
                "image/png", fis);
        String testBucket = "testBucket";
        String urlString = "http://" + fileName + ".com";

        FieldUtils.writeDeclaredField(amazonS3ClientService, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientService, "awsS3Bucket", testBucket, true);

        URL url = new URL(urlString);

        when(amazonS3.getUrl(eq(testBucket), any())).thenReturn(url);

        String imageUrl = (amazonS3ClientService.uploadFileToS3Bucket(image, true));

        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(eq(testBucket), Mockito.contains(fileName));
        assertEquals(urlString, imageUrl);
    }

    @Test
    void uploadFileToS3BucketExceptionTest() throws IOException, IllegalAccessException {
        String originalFileName = "inspection-violation-test-image";
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", originalFileName + ".png",
                "image/png", fis);
        String testBucket = "testBucket";
        FieldUtils.writeDeclaredField(amazonS3ClientService, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientService, "awsS3Bucket", testBucket, true);
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenThrow(new AmazonServiceException(""));

        assertThrows(AmazonServiceException.class, () -> amazonS3ClientService.uploadFileToS3Bucket(image, true));
    }

    @Test
    void deleteFileFromS3BucketTest() throws IllegalAccessException {
        String urlString = "http://s3.com/filename.png";
        String testBucket = "testBucket";
        FieldUtils.writeDeclaredField(amazonS3ClientService, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientService, "awsS3Bucket", testBucket, true);

        amazonS3ClientService.deleteFileFromS3Bucket(urlString);

        verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFileFromS3BucketExceptionTest() throws IllegalAccessException {
        String urlString = "http://s3.com/filename.png";
        String testBucket = "testBucket";
        FieldUtils.writeDeclaredField(amazonS3ClientService, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientService, "awsS3Bucket", testBucket, true);

        doThrow(new AmazonServiceException("")).when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(AmazonServiceException.class, () -> amazonS3ClientService.deleteFileFromS3Bucket(urlString));
    }
}
