package com.firerms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.entity.inspections.Inspection;
import com.firerms.entity.inspections.Inspector;
import com.firerms.entity.property.Property;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.repository.InspectionChecklistRepository;
import com.firerms.repository.InspectionRepository;
import com.firerms.repository.InspectorRepository;
import com.firerms.repository.PropertyRepository;
import com.firerms.request.InspectionRequest;
import com.firerms.response.InspectionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class InspectionService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private InspectionChecklistRepository inspectionChecklistRepository;

    @Autowired
    private InspectorRepository inspectorRepository;

    @Autowired
    @Qualifier("ClientService")
    private AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(InspectionService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with id: %s";
    private static final String MUST_BE_NULL_ERROR_MSG = "id must be null for new %s";

    private void validateInspectionForeignKeys(Inspection inspection) {
        Long propertyId = inspection.getPropertyId();
        Property property = propertyRepository.findByPropertyId(propertyId);
        if (property == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Property", propertyId);
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        Long inspectionChecklistId = inspection.getInspectionChecklistId();
        InspectionChecklist inspectionChecklistInDb = inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistId);
        if (inspectionChecklistInDb == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist", inspectionChecklistId);
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        Long inspectorId = inspection.getInspectorId();
        Inspector inspector = inspectorRepository.findByInspectorId(inspectorId);
        if (inspector == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspector", inspectorId);
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
    }

    public InspectionResponse createInspection(InspectionRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - createInspection request: {}", requestString);
        Inspection inspection = request.getInspection();
        if (inspection.getInspectionId() != null) {
            String errorMessage = String.format(MUST_BE_NULL_ERROR_MSG, "Inspection");
            LOG.error("Inspection Service - createInspection request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        validateInspectionForeignKeys(inspection);
        inspection = inspectionRepository.save(inspection);
        InspectionResponse inspectionResponse = new InspectionResponse(inspection);
        String responseString = new ObjectMapper().writeValueAsString(inspectionResponse);
        LOG.info("Inspection Service - successfully created Inspection: {}", responseString);
        return inspectionResponse;
    }

    public InspectionResponse findInspectionById(Long inspectionId) throws JsonProcessingException {
        LOG.info("Inspection Service - findInspectionById request: {}", inspectionId);
        Inspection inspection = inspectionRepository.findByInspectionId(inspectionId);
        if (inspection == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - findInspectionById request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        InspectionResponse inspectionResponse = new InspectionResponse(inspection);
        String responseString = new ObjectMapper().writeValueAsString(inspectionResponse);
        LOG.info("Inspection Service - successfully found Inspection: {}", responseString);
        return inspectionResponse;
    }

    public InspectionResponse updateInspection(InspectionRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - updateInspection request: {}", requestString);
        Inspection inspection = request.getInspection();
        Long inspectionId = inspection.getInspectionId();
        Inspection inspectionInDB = inspectionRepository.findByInspectionId(inspectionId);
        if (inspectionInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - updateInspection request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        validateInspectionForeignKeys(inspection);
        inspection = inspectionRepository.save(inspection);
        InspectionResponse inspectionResponse = new InspectionResponse(inspection);
        String responseString = new ObjectMapper().writeValueAsString(inspectionResponse);
        LOG.info("Inspection Service - successfully updated Inspection: {}", responseString);
        return inspectionResponse;
    }

    public void deleteInspection(Long inspectionId) {
        LOG.info("Inspection Service - deleteInspection request: {}", inspectionId);
        Inspection inspection = inspectionRepository.findByInspectionId(inspectionId);
        if (inspection == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - deleteInspection request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionRepository.delete(inspection);
        LOG.info("Inspection Service - successfully deleted Inspection: {}", inspectionId);
    }
}
