package com.firerms.service.unit;

import com.amazonaws.AmazonServiceException;
import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.entity.checklists.InspectionViolationImageUrl;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.repository.*;
import com.firerms.service.InspectionViolationImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest
public class InspectionViolationImageServiceUnitTests {

    @Autowired
    private InspectionViolationImageService inspectionViolationImageService;

    @MockBean
    private InspectionViolationRepository inspectionViolationRepository;

    @MockBean
    private InspectionViolationImageUrlRepository inspectionViolationImageUrlRepository;

    @MockBean
    AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    private final Long testFdid = 1L;

    @BeforeEach
    void setCurrentTenant() {
        TenantContext.setCurrentTenant(testFdid.toString());
    }

    @Transactional
    @Test
    void addInspectionViolationImageTest() throws IOException {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", "inspection-violation-test-image.jpg",
                "image/png", fis);
        String imageUrl = "imageUrl";
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(amazonS3ClientServiceInterface.uploadFileToS3Bucket(eq(image), eq(true))).thenReturn(imageUrl);

        inspectionViolationImageService.addInspectionViolationImage(image, inspectionViolation.getInspectionViolationId());

        assertEquals(imageUrl, inspectionViolation.getInspectionViolationImageUrlList().get(0).getImageUrl());
    }

    @Transactional
    @Test
    void addInspectionViolationImageWorkOrderDoesNotExistTest() throws IOException {
        Long inspectionViolationId = 1L;
        String errorMessage = "Inspection Violation not found with id " + inspectionViolationId;
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", "inspection-violation-test-image.jpg",
                "image/png", fis);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> inspectionViolationImageService.addInspectionViolationImage(image, inspectionViolationId));
    }

    @Transactional
    @Test
    void addInspectionViolationImageNullImageTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);

        assertThrows(EntityValidationException.class, () -> inspectionViolationImageService.addInspectionViolationImage(null, inspectionViolation.getInspectionViolationId()));
    }

    @Transactional
    @Test
    void addInspectionViolationImageInvalidImageTest() throws IOException {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/illegal-file-type.json");
        MockMultipartFile image = new MockMultipartFile("image", "illegal-file-type.json",
                "application/json", fis);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);

        assertThrows(EntityValidationException.class, () -> inspectionViolationImageService.addInspectionViolationImage(image, inspectionViolation.getInspectionViolationId()));
    }

    @Transactional
    @Test
    void addInspectionViolationImageCannotSaveImageTest() throws IOException {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        FileInputStream fis = new FileInputStream("src/test/java/helpers/images/inspection-violation-test-image.jpg");
        MockMultipartFile image = new MockMultipartFile("image", "inspection-violation-test-image.jpg",
                "image/png", fis);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);
        when(amazonS3ClientServiceInterface.uploadFileToS3Bucket(eq(image), eq(true))).thenThrow(new AmazonServiceException(""));

        assertThrows(AmazonServiceException.class, () -> inspectionViolationImageService.addInspectionViolationImage(image, inspectionViolation.getInspectionViolationId()));
    }

    @Transactional
    @Test
    void deleteWorkOrderImageTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        String imageUrl = "imageUrl";
        InspectionViolationImageUrl inspectionViolationImageUrl = new InspectionViolationImageUrl(1L, inspectionViolation.getInspectionViolationId(), imageUrl, testFdid);
        inspectionViolation.addInspectionViolationImageUrl(inspectionViolationImageUrl);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);

        inspectionViolationImageService.deleteInspectionViolationImage(imageUrl, inspectionViolation.getInspectionViolationId());

        verify(amazonS3ClientServiceInterface, times(1)).deleteFileFromS3Bucket(imageUrl);
        verify(inspectionViolationImageUrlRepository, times(1)).delete(inspectionViolationImageUrl);
    }

    @Transactional
    @Test
    void deleteWorkOrderImageWorkOrderDoesNotExistTest() {
        Long inspectionViolationId = 1L;
        String imageUrl = "imageUrl";
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> inspectionViolationImageService.deleteInspectionViolationImage(imageUrl, inspectionViolationId));
    }

    @Transactional
    @Test
    void deleteWorkOrderImageNullImageUrlTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);

        assertThrows(EntityValidationException.class, () -> inspectionViolationImageService.deleteInspectionViolationImage(null, inspectionViolation.getInspectionViolationId()));
    }

    @Transactional
    @Test
    void deleteWorkOrderImageCannotDeleteImageTest() {
        InspectionViolation inspectionViolation = new InspectionViolation(1L, 1L, 1L,
                1L, 1L, "description", "location", "narrative",
                new Date(), new Date(), new Date(), testFdid);
        String imageUrl = "imageUrl";
        InspectionViolationImageUrl inspectionViolationImageUrl = new InspectionViolationImageUrl(1L, inspectionViolation.getInspectionViolationId(), imageUrl, testFdid);
        inspectionViolation.addInspectionViolationImageUrl(inspectionViolationImageUrl);
        when(inspectionViolationRepository.findByInspectionViolationId(inspectionViolation.getInspectionViolationId())).thenReturn(inspectionViolation);

        doThrow(new AmazonServiceException("")).when(amazonS3ClientServiceInterface).deleteFileFromS3Bucket(eq(imageUrl));

        assertThrows(AmazonServiceException.class, () -> inspectionViolationImageService.deleteInspectionViolationImage(imageUrl, inspectionViolation.getInspectionViolationId()));
    }

}
