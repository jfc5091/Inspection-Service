package com.firerms.service;

import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.entity.checklists.InspectionViolationImageUrl;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.repository.InspectionViolationImageUrlRepository;
import com.firerms.repository.InspectionViolationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
public class InspectionViolationImageService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private InspectionViolationImageUrlRepository inspectionViolationImageUrlRepository;

    @Autowired
    private InspectionViolationRepository inspectionViolationRepository;

    @Autowired
    @Qualifier("ClientService")
    private AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(InspectionViolationImageService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with id: %s";
    private static final String VALIDATION_ERROR_MSG = "validation error: %s";

    private void validateImage(MultipartFile image) {
        try {
            InputStream input = image.getInputStream();
            if (ImageIO.read(input) == null) {
                String errorMessage = "illegal image type";
                LOG.error("Inspection Service - validateImage {}", errorMessage);
                throw new EntityValidationException(String.format(VALIDATION_ERROR_MSG, errorMessage));
            }
        } catch (Exception e) {
            String errorMessage = "illegal image type";
            LOG.error("Inspection Service - validateImage {}", errorMessage);
            throw new EntityValidationException(String.format(VALIDATION_ERROR_MSG, errorMessage));
        }
    }

    private void uploadImages(MultipartFile image, InspectionViolation inspectionViolation) throws IOException {
        validateImage(image);
        String imageUrl = this.amazonS3ClientServiceInterface.uploadFileToS3Bucket(image, true);
        InspectionViolationImageUrl inspectionViolationImageUrl = new InspectionViolationImageUrl(inspectionViolation.getInspectionViolationId(), imageUrl,  inspectionViolation.getFdid());
        inspectionViolationImageUrlRepository.save(inspectionViolationImageUrl);
        inspectionViolation.addInspectionViolationImageUrl(inspectionViolationImageUrl);
        LOG.info("Inspection Service - successfully added {} for inspection violation {}", image, inspectionViolation.getInspectionViolationId());
    }

    private void deleteImage(String imageUrl, InspectionViolation inspectionViolation) {
        this.amazonS3ClientServiceInterface.deleteFileFromS3Bucket(imageUrl);
        for (InspectionViolationImageUrl inspectionViolationImageUrl : inspectionViolation.getInspectionViolationImageUrlList()) {
            if (inspectionViolationImageUrl.getImageUrl().equals(imageUrl)) {
                inspectionViolationImageUrlRepository.delete(inspectionViolationImageUrl);
                inspectionViolation.deleteInspectionViolationImageUrl(inspectionViolationImageUrl);
                LOG.info("Inspection Service - successfully deleted {} from inspection violation {}", imageUrl, inspectionViolation.getInspectionViolationId());
                break;
            }
        }
    }

    public void addInspectionViolationImage(MultipartFile image, Long inspectionViolationId) throws IOException {
        LOG.info("Inspection Service - addInspectionViolationImage request: for inspection violation {} with images {}", inspectionViolationId, image);
        InspectionViolation inspectionViolationInDB = inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId);
        if (inspectionViolationInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Violation", inspectionViolationId);
            LOG.error("Inspection Service - addInspectionViolationImage request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        if (image != null && !Objects.equals(image.getOriginalFilename(), "")) {
            uploadImages(image, inspectionViolationInDB);
        }
        else {
            String errorMessage = String.format(VALIDATION_ERROR_MSG, "Image is null or has empty OriginalFilename");
            LOG.error("Inspection Service - addInspectionViolationImage request: {}", errorMessage);
            throw new EntityValidationException(errorMessage);
        }
    }

    public void deleteInspectionViolationImage(String imageUrl, Long inspectionViolationId) {
        LOG.info("Inspection Service - deleteInspectionViolationImage request: for inspection violation {} with images {}", inspectionViolationId, imageUrl);
        InspectionViolation inspectionViolationInDB = inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId);
        if (inspectionViolationInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Violation", inspectionViolationId);
            LOG.error("Inspection Service - deleteInspectionViolationImage request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        if (imageUrl != null) {
            deleteImage(imageUrl, inspectionViolationInDB);
        }
        else {
            String errorMessage = String.format(VALIDATION_ERROR_MSG, "Image url is null");
            LOG.error("Inspection Service - deleteInspectionViolationImage request: {}", errorMessage);
            throw new EntityValidationException(errorMessage);
        }
    }
}
