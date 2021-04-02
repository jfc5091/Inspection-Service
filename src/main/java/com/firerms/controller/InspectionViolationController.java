package com.firerms.controller;

import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspection/violation")
public class InspectionViolationController {

    @Autowired
    private InspectionViolationService inspectionViolationService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;
}
