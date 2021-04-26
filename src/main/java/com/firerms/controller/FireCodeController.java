package com.firerms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.EntityValidationException;
import com.firerms.exception.IdNotNullException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.request.FireCodeRequest;
import com.firerms.response.FireCodeResponse;
import com.firerms.service.FireCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/inspection/firecode")
public class FireCodeController {

    @Autowired
    private FireCodeService fireCodeService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping("/create")
    public ResponseEntity<Object> createFireCode(@RequestBody FireCodeRequest request) throws JsonProcessingException {
        try {
            FireCodeResponse fireCodeResponse = fireCodeService.createFireCode(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(fireCodeResponse);
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
    public ResponseEntity<Object> getFireCode(@PathVariable("id") Long fireCodeId) throws JsonProcessingException {
        try {
            FireCodeResponse fireCodeResponse = fireCodeService.findFireCodeById(fireCodeId);
            return ResponseEntity.status(HttpStatus.OK).body(fireCodeResponse);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateFireCode(@RequestBody FireCodeRequest request) throws JsonProcessingException {
        try {
            FireCodeResponse fireCodeResponse = fireCodeService.updateFireCode(request);
            return ResponseEntity.status(HttpStatus.OK).body(fireCodeResponse);
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
    public ResponseEntity<Object> deleteFireCode(@PathVariable("id") Long fireCodeId) {
        try {
            fireCodeService.deleteFireCode(fireCodeId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
    }
}
