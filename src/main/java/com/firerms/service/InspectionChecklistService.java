package com.firerms.service;

import com.firerms.amazon.s3.AmazonS3ClientServiceInterface;
import com.firerms.repository.InspectionChecklistRepository;
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
    private static final String VALIDATION_ERROR_MSG = "validation error: %s";
}
