package com.firerms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.exception.IdNotNullException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.request.InspectionChecklistItemRequest;
import com.firerms.response.InspectionChecklistItemResponse;
import com.firerms.service.InspectionChecklistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/inspection/checklistitem")
public class InspectionChecklistItemController {

    @Autowired
    private InspectionChecklistItemService inspectionChecklistItemService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping("/create")
    public ResponseEntity<Object> createInspectionChecklistItem(@RequestBody InspectionChecklistItemRequest request) throws JsonProcessingException {
        try {
            InspectionChecklistItemResponse inspectionChecklistItemResponse = inspectionChecklistItemService.createInspectionChecklistItem(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(inspectionChecklistItemResponse);
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
    public ResponseEntity<Object> getInspectionChecklistItem(@PathVariable("id") Long inspectionChecklistItemId) throws JsonProcessingException {
        try {
            InspectionChecklistItemResponse inspectionChecklistItemResponse = inspectionChecklistItemService.findInspectionChecklistItemById(inspectionChecklistItemId);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionChecklistItemResponse);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateInspectionChecklistItem(@RequestBody InspectionChecklistItemRequest request) throws JsonProcessingException {
        try {
            InspectionChecklistItemResponse inspectionChecklistItemResponse = inspectionChecklistItemService.updateInspectionChecklistItem(request);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionChecklistItemResponse);
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
    public ResponseEntity<Object> deleteInspectionChecklistItem(@PathVariable("id") Long inspectionChecklistItemId) {
        try {
            inspectionChecklistItemService.deleteInspectionChecklistItem(inspectionChecklistItemId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }
}
