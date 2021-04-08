package com.firerms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.inspections.Inspection;
import com.firerms.entity.inspections.InspectionAction;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.repository.InspectionActionRepository;
import com.firerms.repository.InspectionRepository;
import com.firerms.request.InspectionActionRequest;
import com.firerms.response.InspectionActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class InspectionActionService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private InspectionActionRepository inspectionActionRepository;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    @Qualifier("ClientService")
    private AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(InspectionActionService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with id: %s";
    private static final String MUST_BE_NULL_ERROR_MSG = "id must be null for new %s";

    public InspectionActionResponse createInspectionAction(InspectionActionRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - createInspectionAction request: {}", requestString);
        InspectionAction inspectionAction = request.getInspectionAction();
        if (inspectionAction.getInspectionActionId() != null) {
            String errorMessage = String.format(MUST_BE_NULL_ERROR_MSG, "Inspection Action");
            LOG.error("Inspection Service - createInspectionAction request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        Long inspectionId = inspectionAction.getInspectionId();
        Inspection inspectionInDb = inspectionRepository.findByInspectionId(inspectionId);
        if(inspectionInDb == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - createInspectionAction request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionAction = inspectionActionRepository.save(inspectionAction);
        InspectionActionResponse inspectionActionResponse = new InspectionActionResponse(inspectionAction);
        String responseString = new ObjectMapper().writeValueAsString(inspectionActionResponse);
        LOG.info("Inspection Service - successfully created Inspection Action: {}", responseString);
        return inspectionActionResponse;
    }

    public InspectionActionResponse findInspectionActionById(Long inspectionActionId) throws JsonProcessingException {
        LOG.info("Inspection Service - findInspectionActionById request: {}", inspectionActionId);
        InspectionAction inspectionAction = inspectionActionRepository.findByInspectionActionId(inspectionActionId);
        if (inspectionAction == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Action", inspectionActionId);
            LOG.error("Inspection Service - findInspectionActionById request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        InspectionActionResponse inspectionActionResponse = new InspectionActionResponse(inspectionAction);
        String responseString = new ObjectMapper().writeValueAsString(inspectionActionResponse);
        LOG.info("Inspection Service - successfully found Inspection Action: {}", responseString);
        return inspectionActionResponse;
    }

    public InspectionActionResponse updateInspectionAction(InspectionActionRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - updateInspectionAction request: {}", requestString);
        InspectionAction inspectionAction = request.getInspectionAction();
        Long inspectionActionId = inspectionAction.getInspectionActionId();
        InspectionAction inspectionActionInDB = inspectionActionRepository.findByInspectionActionId(inspectionActionId);
        if (inspectionActionInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Action", inspectionActionId);
            LOG.error("Inspection Service - updateInspectionAction request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        Long inspectionId = inspectionAction.getInspectionId();
        Inspection inspectionInDb = inspectionRepository.findByInspectionId(inspectionId);
        if(inspectionInDb == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection", inspectionId);
            LOG.error("Inspection Service - createInspectionAction request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionAction = inspectionActionRepository.save(inspectionAction);
        InspectionActionResponse inspectionActionResponse = new InspectionActionResponse(inspectionAction);
        String responseString = new ObjectMapper().writeValueAsString(inspectionActionResponse);
        LOG.info("Inspection Service - successfully updated Inspection Action: {}", responseString);
        return inspectionActionResponse;
    }

    public void deleteInspectionAction(Long inspectionActionId) {
        LOG.info("Inspection Service - deleteInspectionAction request: {}", inspectionActionId);
        InspectionAction inspectionAction = inspectionActionRepository.findByInspectionActionId(inspectionActionId);
        if (inspectionAction == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Action", inspectionActionId);
            LOG.error("Inspection Service - deleteInspectionAction request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionActionRepository.delete(inspectionAction);
        LOG.info("Inspection Service - successfully deleted Inspection Action: {}", inspectionActionId);
    }
}
