package com.firerms.controller;

import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionViolationImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspection/violation/{id}/image")
public class InspectionViolationImageController {

    @Autowired
    private InspectionViolationImageService inspectionViolationImageService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;
}
