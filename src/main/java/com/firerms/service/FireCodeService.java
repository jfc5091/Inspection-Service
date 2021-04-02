package com.firerms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firerms.entity.checklists.FireCode;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.IdNotNullException;
import com.firerms.repository.FireCodeRepository;
import com.firerms.request.FireCodeRequest;
import com.firerms.response.FireCodeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class FireCodeService {

    @PersistenceContext
    public EntityManager entityManager;

    @Autowired
    private FireCodeRepository fireCodeRepository;

    @Autowired
    private RestTemplate template;

    private static final Logger LOG = LoggerFactory.getLogger(FireCodeService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with id: %s";
    private static final String MUST_BE_NULL_ERROR_MSG = "id must be null for new %s";

    public FireCodeResponse createFireCode(FireCodeRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - createFireCode request: {}", requestString);
        FireCode fireCode = request.getFireCode();
        if (fireCode.getFireCodeId() != null) {
            String errorMessage = String.format(MUST_BE_NULL_ERROR_MSG, "Fire Code");
            LOG.error("Inspection Service - createFireCode request: {}", errorMessage);
            throw new IdNotNullException(errorMessage);
        }
        fireCode = fireCodeRepository.save(fireCode);
        FireCodeResponse fireCodeResponse = new FireCodeResponse(fireCode);
        String responseString = new ObjectMapper().writeValueAsString(fireCodeResponse);
        LOG.info("Inspection Service - successfully created Fire Code: {}", responseString);
        return fireCodeResponse;
    }

    public FireCodeResponse findFireCodeById(Long fireCodeId) throws JsonProcessingException {
        LOG.info("Inspection Service - findFireCodeById request: {}", fireCodeId);
        FireCode fireCode = fireCodeRepository.findByFireCodeId(fireCodeId);
        if (fireCode == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Fire Code", fireCodeId);
            LOG.error("Inspection Service - findFireCodeById request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        FireCodeResponse fireCodeResponse = new FireCodeResponse(fireCode);
        String responseString = new ObjectMapper().writeValueAsString(fireCodeResponse);
        LOG.info("Inspection Service - successfully found Fire Code: {}", responseString);
        return fireCodeResponse;
    }

    public FireCodeResponse updateFireCode(FireCodeRequest request) throws JsonProcessingException {
        String requestString = new ObjectMapper().writeValueAsString(request);
        LOG.info("Inspection Service - updateFireCode request: {}", requestString);
        FireCode fireCode = request.getFireCode();
        Long fireCodeId = fireCode.getFireCodeId();
        FireCode fireCodeInDB = fireCodeRepository.findByFireCodeId(fireCodeId);
        if (fireCodeInDB == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Fire Code", fireCodeId);
            LOG.error("Inspection Service - updateFireCode request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        fireCode = fireCodeRepository.save(fireCode);
        FireCodeResponse fireCodeResponse = new FireCodeResponse(fireCode);
        String responseString = new ObjectMapper().writeValueAsString(fireCodeResponse);
        LOG.info("Inspection Service - successfully updated Fire Code: {}", responseString);
        return fireCodeResponse;
    }

    public void deleteFireCode(Long fireCodeId) {
        LOG.info("Inspection Service - deleteFireCode request: {}", fireCodeId);
        FireCode fireCode = fireCodeRepository.findByFireCodeId(fireCodeId);
        if (fireCode == null) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "Fire Code", fireCodeId);
            LOG.error("Inspection Service - deleteFireCode request: {}", errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
        fireCodeRepository.delete(fireCode);
        LOG.info("Inspection Service - successfully deleted Fire Code: {}", fireCodeId);
    }
}
