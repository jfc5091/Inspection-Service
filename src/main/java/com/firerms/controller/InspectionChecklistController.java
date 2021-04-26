package com.firerms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.exception.IdNotNullException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.request.InspectionChecklistRequest;
import com.firerms.response.InspectionChecklistResponse;
import com.firerms.service.InspectionChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/inspection/checklist")
public class InspectionChecklistController {

    @Autowired
    private InspectionChecklistService inspectionChecklistService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping("/create")
    public ResponseEntity<Object> createInspectionChecklist(@RequestBody InspectionChecklistRequest request) throws JsonProcessingException {
        try {
            InspectionChecklistResponse inspectionChecklistResponse = inspectionChecklistService.createInspectionChecklist(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(inspectionChecklistResponse);
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
    public ResponseEntity<Object> getInspectionChecklist(@PathVariable("id") Long inspectionChecklistId) throws JsonProcessingException {
        try {
            InspectionChecklistResponse inspectionChecklistResponse = inspectionChecklistService.findInspectionChecklistById(inspectionChecklistId);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionChecklistResponse);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateInspectionChecklist(@RequestBody InspectionChecklistRequest request) throws JsonProcessingException {
        try {
            InspectionChecklistResponse inspectionChecklistResponse = inspectionChecklistService.updateInspectionChecklist(request);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionChecklistResponse);
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
    public ResponseEntity<Object> deleteInspectionChecklist(@PathVariable("id") Long inspectionChecklistId) {
        try {
            inspectionChecklistService.deleteInspectionChecklist(inspectionChecklistId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }
}
