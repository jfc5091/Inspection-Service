package com.firerms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firerms.entity.inspections.Inspection;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.exception.IdNotNullException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.request.InspectionRequest;
import com.firerms.response.InspectionResponse;
import com.firerms.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/inspection")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping("/create")
    public ResponseEntity<Object> createInspection(@RequestBody InspectionRequest request) throws JsonProcessingException {
        try {
            InspectionResponse inspectionResponse = inspectionService.createInspection(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(inspectionResponse);
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
    public ResponseEntity<Object> getInspection(@PathVariable("id") Long inspectionId) throws JsonProcessingException {
        try {
            InspectionResponse inspectionResponse = inspectionService.findInspectionById(inspectionId);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionResponse);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateInspection(@RequestBody InspectionRequest request) throws JsonProcessingException {
        try {
            InspectionResponse inspectionResponse = inspectionService.updateInspection(request);
            return ResponseEntity.status(HttpStatus.OK).body(inspectionResponse);
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
    public ResponseEntity<Object> deleteInspection(@PathVariable("id") Long inspectionId) {
        try {
            inspectionService.deleteInspection(inspectionId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllInspectionPageable(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                                                              @RequestParam(value = "limit", defaultValue = "30") Integer limit) throws JsonProcessingException {
        Page<Inspection> personnelDeptPage = inspectionService.getAllInspectionPageable(pageNumber, limit);
        return ResponseEntity.status(HttpStatus.OK).body(personnelDeptPage);
    }
}
