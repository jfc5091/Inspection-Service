package com.firerms.controller;

import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspection")
public class InspectionController {

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;
}
