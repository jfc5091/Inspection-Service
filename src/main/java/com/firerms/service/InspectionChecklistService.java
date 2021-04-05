package com.firerms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.repository.InspectionChecklistRepository;
import com.firerms.request.InspectionChecklistRequest;
import com.firerms.response.InspectionChecklistResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class InspectionChecklistService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private InspectionChecklistRepository inspectionChecklistRepository;

    @Autowired
    @Qualifier("ClientService")
    private AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(InspectionChecklistService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with id: %s";
    private static final String MUST_BE_NULL_ERROR_MSG = "id must be null for new %s";

    public InspectionChecklistResponse createInspectionChecklist(InspectionChecklistRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - createInspectionChecklist request: {}", requestString);
        InspectionChecklist inspectionChecklist = request.getInspectionChecklist();
        if (inspectionChecklist.getInspectionChecklistId() != null) {
            String errorMessage = String.format(MUST_BE_NULL_ERROR_MSG, "Inspection Checklist");
            LOG.error("Inspection Service - createInspectionChecklist request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        inspectionChecklist = inspectionChecklistRepository.save(inspectionChecklist);
        InspectionChecklistResponse inspectionChecklistResponse = new InspectionChecklistResponse(inspectionChecklist);
        String responseString = new ObjectMapper().writeValueAsString(inspectionChecklistResponse);
        LOG.info("Inspection Service - successfully created Inspection Checklist: {}", responseString);
        return inspectionChecklistResponse;
    }

    public InspectionChecklistResponse findInspectionChecklistById(Long inspectionChecklistId) throws JsonProcessingException {
        LOG.info("Inspection Service - findInspectionChecklistById request: {}", inspectionChecklistId);
        InspectionChecklist inspectionChecklist = inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistId);
        if (inspectionChecklist == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist", inspectionChecklistId);
            LOG.error("Inspection Service - findInspectionChecklistById request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        InspectionChecklistResponse inspectionChecklistResponse = new InspectionChecklistResponse(inspectionChecklist);
        String responseString = new ObjectMapper().writeValueAsString(inspectionChecklistResponse);
        LOG.info("Inspection Service - successfully found Inspection Checklist: {}", responseString);
        return inspectionChecklistResponse;
    }

    public InspectionChecklistResponse updateInspectionChecklist(InspectionChecklistRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - updateInspectionChecklist request: {}", requestString);
        InspectionChecklist inspectionChecklist = request.getInspectionChecklist();
        Long inspectionChecklistId = inspectionChecklist.getInspectionChecklistId();
        InspectionChecklist inspectionChecklistInDB = inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistId);
        if (inspectionChecklistInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist", inspectionChecklistId);
            LOG.error("Inspection Service - updateInspectionChecklist request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionChecklist = inspectionChecklistRepository.save(inspectionChecklist);
        InspectionChecklistResponse inspectionChecklistResponse = new InspectionChecklistResponse(inspectionChecklist);
        String responseString = new ObjectMapper().writeValueAsString(inspectionChecklistResponse);
        LOG.info("Inspection Service - successfully updated Inspection Checklist: {}", responseString);
        return inspectionChecklistResponse;
    }

    public void deleteInspectionChecklist(Long inspectionChecklistId) {
        LOG.info("Inspection Service - deleteInspectionChecklist request: {}", inspectionChecklistId);
        InspectionChecklist inspectionChecklist = inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistId);
        if (inspectionChecklist == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist", inspectionChecklistId);
            LOG.error("Inspection Service - deleteInspectionChecklist request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionChecklistRepository.delete(inspectionChecklist);
        LOG.info("Inspection Service - successfully deleted Inspection Checklist: {}", inspectionChecklistId);
    }
}
