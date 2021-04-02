package com.firerms.exception;

import com.amazonaws.AmazonServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class InspectionExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<InspectionError> entityNotFoundException(EntityNotFoundException exception) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        InspectionError inspectionError = new InspectionError(exception.getMessage(), status.value(), ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(inspectionError, status);
    }

    @ExceptionHandler(IdNotNullException.class)
    public ResponseEntity<InspectionError> idNotNullException(IdNotNullException exception) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        InspectionError inspectionError = new InspectionError(exception.getMessage(), status.value(), ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(inspectionError, status);
    }

    @ExceptionHandler(EntityValidationException.class)
    public ResponseEntity<InspectionError> entityValidationException(EntityValidationException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        InspectionError inspectionError = new InspectionError(exception.getMessage(), status.value(), ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(inspectionError, status);
    }

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<InspectionError> amazonServiceException(AmazonServiceException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode());
        InspectionError personnelError = new InspectionError(exception.getMessage(), status.value(), ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(personnelError, status);
    }
}
