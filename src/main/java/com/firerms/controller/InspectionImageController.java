package com.firerms.controller;

import com.amazonaws.AmazonServiceException;
import com.firerms.exception.EntityNotFoundException;
import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/inspection/{id}/image")
public class InspectionImageController {

    @Autowired
    private InspectionImageService inspectionImageService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;

    @PostMapping(value = "/add", consumes ={ MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> addInspectionImage(@RequestPart(value = "image") MultipartFile image,
                                                    @RequestPart(value = "signatureType") String type,
                                                    @PathVariable("id") Long inspectionId) throws IOException {
        try {
            inspectionImageService.addInspectionImage(image, inspectionId, type);
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
    public ResponseEntity<Object> deleteInspectionImage(@RequestPart(value = "imageUrl") String imageUrl,
                                                       @RequestPart(value = "signatureType") String type,
                                                       @PathVariable("id") Long inspectionId) {
        try {
            inspectionImageService.deleteInspectionImage(imageUrl, inspectionId, type);
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
