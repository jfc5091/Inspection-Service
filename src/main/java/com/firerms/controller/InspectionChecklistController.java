package com.firerms.controller;

import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspection/checklist")
public class InspectionChecklistController {

    @Autowired
    private InspectionChecklistService inspectionChecklistService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;
}
