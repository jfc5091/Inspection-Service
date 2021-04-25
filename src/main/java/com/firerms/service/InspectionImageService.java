package com.firerms.service;

import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.inspections.Inspection;
import com.firerms.entity.inspections.InspectionImageUrl;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.repository.InspectionImageUrlRepository;
import com.firerms.repository.InspectionRepository;
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
public class InspectionImageService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private InspectionImageUrlRepository inspectionImageUrlRepository;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    @Qualifier("ClientService")
    private AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(InspectionImageService.class);

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

    private void uploadImages(MultipartFile image, Inspection inspection, String signatureType) throws IOException {
        validateImage(image);
        String imageUrl = this.amazonS3ClientServiceInterface.uploadFileToS3Bucket(image, true);
        InspectionImageUrl inspectionImageUrl = new InspectionImageUrl(inspection.getInspectionId(), imageUrl,  inspection.getFdid());
        inspectionImageUrlRepository.save(inspectionImageUrl);
        if(signatureType.equals("inspector")) {
            inspection.setInspectorSignatureUrl(inspectionImageUrl.getImageUrl());
        }
        else {
            inspection.setOccupantSignatureUrl(inspectionImageUrl.getImageUrl());
        }
        LOG.info("Inspection Service - successfully added {} for inspection {}", image, inspection.getInspectionId());
    }

    private void deleteImage(String imageUrl, Inspection inspection, String signatureType) {
        InspectionImageUrl inspectionImageUrl = inspectionImageUrlRepository.findByImageUrl(imageUrl);
        this.amazonS3ClientServiceInterface.deleteFileFromS3Bucket(imageUrl);
        if(signatureType.equals("inspector")) {
            inspection.setInspectorSignatureUrl(null);
        }
        else {
            inspection.setOccupantSignatureUrl(null);
        }
        inspectionImageUrlRepository.delete(inspectionImageUrl);
        LOG.info("Inspection Service - successfully deleted {} from inspection {}", imageUrl, inspection.getInspectionId());
    }

    public void addInspectionImage(MultipartFile image, Long inspectionId, String signatureType) throws IOException {
        LOG.info("Inspection Service - addInspectionImage request: for inspection {} with images {}", inspectionId, image);
        Inspection inspectionInDB = inspectionRepository.findByInspectionId(inspectionId);
        if (inspectionInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - addInspectionImage request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        if (image != null && !Objects.equals(image.getOriginalFilename(), "")) {
            uploadImages(image, inspectionInDB, signatureType);
        }
        else {
            String errorMessage = String.format(VALIDATION_ERROR_MSG, "Image is null or has empty OriginalFilename");
            LOG.error("Inspection Service - addInspectionImage request: {}", errorMessage);
            throw new EntityValidationException(errorMessage);
        }
    }

    public void deleteInspectionImage(String imageUrl, Long inspectionId, String signatureType) {
        LOG.info("Inspection Service - deleteInspectionImage request: for inspection {} with images {}", inspectionId, imageUrl);
        Inspection inspectionInDB = inspectionRepository.findByInspectionId(inspectionId);
        if (inspectionInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - deleteInspectionImage request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        if (imageUrl != null) {
            deleteImage(imageUrl, inspectionInDB, signatureType);
        }
        else {
            String errorMessage = String.format(VALIDATION_ERROR_MSG, "Image url is null");
            LOG.error("Inspection Service - deleteInspectionImage request: {}", errorMessage);
            throw new EntityValidationException(errorMessage);
        }
    }
}
