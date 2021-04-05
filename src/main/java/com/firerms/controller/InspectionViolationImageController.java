package com.firerms.controller;

import com.amazonaws.AmazonServiceException;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionViolationImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/inspection/violation/{id}/image")
public class InspectionViolationImageController {

    @Autowired
    private InspectionViolationImageService inspectionViolationImageService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping(value = "/add", consumes ={ MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> addWorkOrderImage(@RequestPart(value = "image") MultipartFile image,
                                                    @PathVariable("id") Long workOrderId) throws IOException {
        try {
            inspectionViolationImageService.addInspectionViolationImage(image, workOrderId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
        catch (AmazonServiceException e) {
            return ResponseEntity.status(inspectionExceptionHandler.amazonServiceException(e).getStatusCode())
                    .body(inspectionExceptionHandler.amazonServiceException(e).getBody());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteWorkOrderImage(@RequestBody String imageUrl,
                                                       @PathVariable("id") Long workOrderId) {
        try {
            inspectionViolationImageService.deleteInspectionViolationImage(imageUrl, workOrderId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(inspectionExceptionHandler.entityNotFoundException(e).getStatusCode())
                    .body(inspectionExceptionHandler.entityNotFoundException(e).getBody());
        }
        catch (AmazonServiceException e) {
            return ResponseEntity.status(inspectionExceptionHandler.amazonServiceException(e).getStatusCode())
                    .body(inspectionExceptionHandler.amazonServiceException(e).getBody());
        }
    }
}
