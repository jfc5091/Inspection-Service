package com.firerms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.exception.IdNotNullException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.request.InspectionActionRequest;
import com.firerms.response.InspectionActionResponse;
import com.firerms.service.InspectionActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/inspection/action")
public class InspectionActionController {

    @Autowired
    private InspectionActionService inspectionActionService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping("/create")
    public ResponseEntity<Object> createInspectionAction(@RequestBody InspectionActionRequest request) throws JsonProcessingException {
        try {
            InspectionActionResponse inspectionActionResponse = inspectionActionService.createInspectionAction(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(inspectionActionResponse);
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
    public ResponseEntity<Object> getInspectionAction(@PathVariable("id") Long inspectionActionId) throws JsonProcessingException {
        try {
            InspectionActionResponse inspectionActionResponse = inspectionActionService.findInspectionActionById(inspectionActionId);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionActionResponse);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateInspectionAction(@RequestBody InspectionActionRequest request) throws JsonProcessingException {
        try {
            InspectionActionResponse inspectionActionResponse = inspectionActionService.updateInspectionAction(request);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionActionResponse);
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
    public ResponseEntity<Object> deleteInspectionAction(@PathVariable("id") Long inspectionActionId) {
        try {
            inspectionActionService.deleteInspectionAction(inspectionActionId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }
}
