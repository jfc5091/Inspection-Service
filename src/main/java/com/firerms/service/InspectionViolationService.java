package com.firerms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.checklists.FireCode;
import com.firerms.entity.checklists.InspectionChecklistItem;
import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.entity.checklists.InspectionViolationStatus;
import com.firerms.entity.inspections.Inspection;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.repository.*;
import com.firerms.request.InspectionViolationRequest;
import com.firerms.response.InspectionViolationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class InspectionViolationService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private InspectionViolationRepository inspectionViolationRepository;

    @Autowired
    private FireCodeRepository fireCodeRepository;

    @Autowired
    private InspectionViolationStatusRepository inspectionViolationStatusRepository;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionChecklistItemRepository inspectionChecklistItemRepository;

    @Autowired
    @Qualifier("ClientService")
    private AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(InspectionViolationService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with id: %s";
    private static final String MUST_BE_NULL_ERROR_MSG = "id must be null for new %s";

    private void validateInspectionViolationForeignKeys(InspectionViolation inspectionViolation) {
        Long fireCodeId = inspectionViolation.getFireCodeId();
        FireCode fireCode = fireCodeRepository.findByFireCodeId(fireCodeId);
        if (fireCode == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Fire code", fireCodeId);
            LOG.error("Inspection Service - createInspectionViolation request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        Long inspectionViolationStatusId = inspectionViolation.getInspectionViolationStatusId();
        InspectionViolationStatus inspectionViolationStatus = inspectionViolationStatusRepository.findByInspectionViolationStatusId(inspectionViolationStatusId);
        if (inspectionViolationStatus == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Violation Status", inspectionViolationStatusId);
            LOG.error("Inspection Service - createInspectionViolation request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        Long inspectionId = inspectionViolation.getInspectionId();
        Inspection inspection  = inspectionRepository.findByInspectionId(inspectionId);
        if (inspection == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - createInspectionViolation request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        Long inspectionChecklistItemId = inspectionViolation.getInspectionChecklistItemId();
        InspectionChecklistItem inspectionChecklistItem  = inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionChecklistItemId);
        if (inspectionChecklistItem == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist Item", inspectionChecklistItemId);
            LOG.error("Inspection Service - createInspectionViolation request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
    }

    public InspectionViolationResponse createInspectionViolation(InspectionViolationRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - createInspectionViolation request: {}", requestString);
        InspectionViolation inspectionViolation = request.getInspectionViolation();
        if (inspectionViolation.getInspectionViolationId() != null) {
            String errorMessage = String.format(MUST_BE_NULL_ERROR_MSG, "Inspection Violation");
            LOG.error("Inspection Service - createInspectionViolation request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        validateInspectionViolationForeignKeys(inspectionViolation);
        inspectionViolation = inspectionViolationRepository.save(inspectionViolation);
        InspectionViolationResponse inspectionViolationResponse = new InspectionViolationResponse(inspectionViolation);
        String responseString = new ObjectMapper().writeValueAsString(inspectionViolationResponse);
        LOG.info("Inspection Service - successfully created Inspection Violation: {}", responseString);
        return inspectionViolationResponse;
    }

    public InspectionViolationResponse findInspectionViolationById(Long inspectionViolationId) throws JsonProcessingException {
        LOG.info("Inspection Service - findInspectionViolationById request: {}", inspectionViolationId);
        InspectionViolation inspectionViolation = inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId);
        if (inspectionViolation == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Violation", inspectionViolationId);
            LOG.error("Inspection Service - findInspectionViolationById request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        InspectionViolationResponse inspectionViolationResponse = new InspectionViolationResponse(inspectionViolation);
        String responseString = new ObjectMapper().writeValueAsString(inspectionViolationResponse);
        LOG.info("Inspection Service - successfully found Inspection Violation: {}", responseString);
        return inspectionViolationResponse;
    }

    public InspectionViolationResponse updateInspectionViolation(InspectionViolationRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - updateInspectionViolation request: {}", requestString);
        InspectionViolation inspectionViolation = request.getInspectionViolation();
        Long inspectionViolationId = inspectionViolation.getInspectionViolationId();
        InspectionViolation inspectionViolationInDB = inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId);
        if (inspectionViolationInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Violation", inspectionViolationId);
            LOG.error("Inspection Service - updateInspectionViolation request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        validateInspectionViolationForeignKeys(inspectionViolation);
        inspectionViolation = inspectionViolationRepository.save(inspectionViolation);
        InspectionViolationResponse inspectionViolationResponse = new InspectionViolationResponse(inspectionViolation);
        String responseString = new ObjectMapper().writeValueAsString(inspectionViolationResponse);
        LOG.info("Inspection Service - successfully updated Inspection Violation: {}", responseString);
        return inspectionViolationResponse;
    }

    public void deleteInspectionViolation(Long inspectionViolationId) {
        LOG.info("Inspection Service - deleteInspectionViolation request: {}", inspectionViolationId);
        InspectionViolation inspectionViolation = inspectionViolationRepository.findByInspectionViolationId(inspectionViolationId);
        if (inspectionViolation == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Violation", inspectionViolationId);
            LOG.error("Inspection Service - deleteInspectionViolation request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionViolationRepository.delete(inspectionViolation);
        LOG.info("Inspection Service - successfully deleted Inspection Violation: {}", inspectionViolationId);
    }
}
