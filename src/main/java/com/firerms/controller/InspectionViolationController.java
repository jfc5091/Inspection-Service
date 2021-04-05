package com.firerms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.exception.IdNotNullException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.request.InspectionViolationRequest;
import com.firerms.response.InspectionViolationResponse;
import com.firerms.service.InspectionViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inspection/violation")
public class InspectionViolationController {

    @Autowired
    private InspectionViolationService inspectionViolationService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping("/create")
    public ResponseEntity<Object> createInspectionViolation(@RequestBody InspectionViolationRequest request) throws JsonProcessingException {
        try {
            InspectionViolationResponse inspectionViolationResponse = inspectionViolationService.createInspectionViolation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(inspectionViolationResponse);
        }
        catch (IdNotNullException e) {
            return ResponseEntity.status(inspectionExceptionHandler.idNotNullException(e).getStatusCode())
                    .body(inspectionExceptionHandler.idNotNullException(e).getBody());
        }
        catch (EntityValidationException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityValidationException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityValidationException(e).getBody());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getInspectionViolation(@PathVariable("id") Long inspectionViolationId) throws JsonProcessingException {
        try {
            InspectionViolationResponse inspectionViolationResponse = inspectionViolationService.findInspectionViolationById(inspectionViolationId);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionViolationResponse);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateInspectionViolation(@RequestBody InspectionViolationRequest request) throws JsonProcessingException {
        try {
            InspectionViolationResponse inspectionViolationResponse = inspectionViolationService.updateInspectionViolation(request);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionViolationResponse);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
        catch (EntityValidationException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityValidationException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityValidationException(e).getBody());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteInspectionViolation(@PathVariable("id") Long inspectionViolationId) {
        try {
            inspectionViolationService.deleteInspectionViolation(inspectionViolationId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }
}
