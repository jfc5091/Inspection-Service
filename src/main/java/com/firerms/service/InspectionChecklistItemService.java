package com.firerms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.entity.checklists.FireCode;
import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.entity.checklists.InspectionChecklistItem;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.repository.FireCodeRepository;
import com.firerms.repository.InspectionChecklistItemRepository;
import com.firerms.repository.InspectionChecklistRepository;
import com.firerms.request.InspectionChecklistItemRequest;
import com.firerms.response.InspectionChecklistItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class InspectionChecklistItemService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private InspectionChecklistItemRepository inspectionChecklistItemRepository;

    @Autowired
    private InspectionChecklistRepository inspectionChecklistRepository;

    @Autowired
    private FireCodeRepository fireCodeRepository;

    @Autowired
    @Qualifier("ClientService")
    private AmazonS3ClientServiceInterface amazonS3ClientServiceInterface;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(InspectionChecklistItemService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with id: %s";
    private static final String MUST_BE_NULL_ERROR_MSG = "id must be null for new %s";

    public InspectionChecklistItemResponse createInspectionChecklistItem(InspectionChecklistItemRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - createInspectionChecklistItem request: {}", requestString);
        InspectionChecklistItem inspectionChecklistItem = request.getInspectionChecklistItem();
        if (inspectionChecklistItem.getInspectionChecklistItemId() != null) {
            String errorMessage = String.format(MUST_BE_NULL_ERROR_MSG, "Inspection Checklist Item");
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        Long inspectionChecklistId = inspectionChecklistItem.getInspectionChecklistId();
        InspectionChecklist inspectionChecklistInDb = inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistId);
        if (inspectionChecklistInDb == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist", inspectionChecklistId);
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        Long fireCodeId = inspectionChecklistItem.getFireCodeId();
        FireCode fireCodeInDb = fireCodeRepository.findByFireCodeId(fireCodeId);
        if (fireCodeInDb == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Fire Code", fireCodeId);
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionChecklistItem = inspectionChecklistItemRepository.save(inspectionChecklistItem);
        InspectionChecklistItemResponse inspectionChecklistItemResponse = new InspectionChecklistItemResponse(inspectionChecklistItem);
        String responseString = new ObjectMapper().writeValueAsString(inspectionChecklistItemResponse);
        LOG.info("Inspection Service - successfully created Inspection Checklist Item: {}", responseString);
        return inspectionChecklistItemResponse;
    }

    public InspectionChecklistItemResponse findInspectionChecklistItemById(Long inspectionChecklistItemId) throws JsonProcessingException {
        LOG.info("Inspection Service - findInspectionChecklistItemById request: {}", inspectionChecklistItemId);
        InspectionChecklistItem inspectionChecklistItem = inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionChecklistItemId);
        if (inspectionChecklistItem == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist Item", inspectionChecklistItemId);
            LOG.error("Inspection Service - findInspectionChecklistItemById request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        InspectionChecklistItemResponse inspectionChecklistItemResponse = new InspectionChecklistItemResponse(inspectionChecklistItem);
        String responseString = new ObjectMapper().writeValueAsString(inspectionChecklistItemResponse);
        LOG.info("Inspection Service - successfully found Inspection Checklist Item: {}", responseString);
        return inspectionChecklistItemResponse;
    }

    public InspectionChecklistItemResponse updateInspectionChecklistItem(InspectionChecklistItemRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - updateInspectionChecklistItem request: {}", requestString);
        InspectionChecklistItem inspectionChecklistItem = request.getInspectionChecklistItem();
        Long inspectionChecklistItemId = inspectionChecklistItem.getInspectionChecklistItemId();
        InspectionChecklistItem inspectionChecklistItemInDB = inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionChecklistItemId);
        if (inspectionChecklistItemInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist Item", inspectionChecklistItemId);
            LOG.error("Inspection Service - updateInspectionChecklistItem request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        Long inspectionChecklistId = inspectionChecklistItem.getInspectionChecklistId();
        InspectionChecklist inspectionChecklistInDb = inspectionChecklistRepository.findByInspectionChecklistId(inspectionChecklistId);
        if (inspectionChecklistInDb == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist", inspectionChecklistId);
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        Long fireCodeId = inspectionChecklistItem.getFireCodeId();
        FireCode fireCodeInDb = fireCodeRepository.findByFireCodeId(fireCodeId);
        if (fireCodeInDb == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Fire Code", fireCodeId);
            LOG.error("Inspection Service - createInspectionChecklistItem request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionChecklistItem = inspectionChecklistItemRepository.save(inspectionChecklistItem);
        InspectionChecklistItemResponse inspectionChecklistItemResponse = new InspectionChecklistItemResponse(inspectionChecklistItem);
        String responseString = new ObjectMapper().writeValueAsString(inspectionChecklistItemResponse);
        LOG.info("Inspection Service - successfully updated Inspection Checklist Item: {}", responseString);
        return inspectionChecklistItemResponse;
    }

    public void deleteInspectionChecklistItem(Long inspectionChecklistItemId) {
        LOG.info("Inspection Service - deleteInspectionChecklistItem request: {}", inspectionChecklistItemId);
        InspectionChecklistItem inspectionChecklistItem = inspectionChecklistItemRepository.findByInspectionChecklistItemId(inspectionChecklistItemId);
        if (inspectionChecklistItem == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Inspection Checklist Item", inspectionChecklistItemId);
            LOG.error("Inspection Service - deleteInspectionChecklistItem request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        inspectionChecklistItemRepository.delete(inspectionChecklistItem);
        LOG.info("Inspection Service - successfully deleted Inspection Checklist Item: {}", inspectionChecklistItemId);
    }
}
