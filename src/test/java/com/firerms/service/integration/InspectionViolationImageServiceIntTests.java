package com.firerms.service.integration;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.entity.checklists.InspectionViolationImageUrl;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.InspectionViolationImageUrlRepository;
import com.firerms.repository.InspectionViolationRepository;
import com.firerms.service.InspectionViolationImageService;
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

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest
public class InspectionViolationImageServiceIntTests {

    @Autowired
    private InspectionViolationImageService inspectionViolationImageService;

    @Autowired
    AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @MockBean
    private InspectionViolationRepository inspectionViolationRepository;

    @MockBean
    private InspectionViolationImageUrlRepository inspectionViolationImageUrlRepository;

    @MockBean
    AmazonS3 amazonS3;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant(testFdid.toString());
    }

    @Transactional
    @Test
    void addWorkOrderImageTest() throws IOException, IllegalAccessException {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", "inspection-violation-test-image.jpg",
                "image/png", fis);
        String imageUrl = "http://" + image.getOriginalFilename() + ".com";
        URL url = new URL(imageUrl);
        String testBucket = "testBucket";
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "awsS3Bucket", testBucket, true);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(amazonS3.getUrl(eq(testBucket), anyString())).thenReturn(url);

        inspectionViolationImageService.addInspectionViolationImage(image, inspectionViolation.getInspectionViolationId());
        String fileNameWithoutType = Objects.requireNonNull(image.getOriginalFilename()).substring(0, image.getOriginalFilename().lastIndexOf("."));

        assertEquals(imageUrl, inspectionViolation.getInspectionViolationImageUrlList().get(0).getImageUrl());
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        verify(amazonS3, times(1)).getUrl(eq(testBucket), Mockito.contains(fileNameWithoutType));
    }

    @Transactional
    @Test
    void addWorkOrderImageCannotSaveImageTest() throws IOException, IllegalAccessException {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", "inspection-violation-test-image.jpg",
                "image/png", fis);
        String testBucket = "testBucket";
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "awsS3Bucket", testBucket, true);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(amazonS3.putObject(any(PutObjectRequest.class))).thenThrow(new AmazonServiceException(""));

        assertThrows(AmazonServiceException.class, () -> inspectionViolationImageService.addInspectionViolationImage(image, inspectionViolation.getInspectionViolationId()));

        assertTrue(inspectionViolation.getInspectionViolationImageUrlList().isEmpty());
        verify(inspectionViolationImageUrlRepository, times(0)).save(any(InspectionViolationImageUrl.class));
    }

    @Transactional
    @Test
    void deleteWorkOrderImageTest() throws IllegalAccessException {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        String imageUrl = "http://imageurl.com/filename";
        String testBucket = "testBucket";
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "awsS3Bucket", testBucket, true);
        InspectionViolationImageUrl inspectionViolationImageUrl = new InspectionViolationImageUrl(1L, inspectionViolation.getInspectionViolationId(), imageUrl, testFdid);
        inspectionViolation.addInspectionViolationImageUrl(inspectionViolationImageUrl);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);

        inspectionViolationImageService.deleteInspectionViolationImage(imageUrl, inspectionViolation.getInspectionViolationId());

        assertTrue(inspectionViolation.getInspectionViolationImageUrlList().isEmpty());
        verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
        verify(inspectionViolationImageUrlRepository, times(1)).delete(inspectionViolationImageUrl);
    }

    @Transactional
    @Test
    void deleteWorkOrderImageCannotDeleteImageTest() throws IllegalAccessException {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        String imageUrl = "http://imageurl.com/filename";
        String testBucket = "testBucket";
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "amazonS3", amazonS3, true);
        FieldUtils.writeDeclaredField(amazonS3ClientServiceInterface, "awsS3Bucket", testBucket, true);
        InspectionViolationImageUrl inspectionViolationImageUrl = new InspectionViolationImageUrl(1L, inspectionViolation.getInspectionViolationId(), imageUrl, testFdid);
        inspectionViolation.addInspectionViolationImageUrl(inspectionViolationImageUrl);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        doThrow(new AmazonServiceException("")).when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

        assertThrows(AmazonServiceException.class, () -> inspectionViolationImageService.deleteInspectionViolationImage(imageUrl, inspectionViolation.getInspectionViolationId()));

        assertFalse(inspectionViolation.getInspectionViolationImageUrlList().isEmpty());
        verify(inspectionViolationImageUrlRepository, times(0)).delete(inspectionViolationImageUrl);
    }

}
