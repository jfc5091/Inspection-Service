package com.firerms.controller;

import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspection/action")
public class InspectionActionController {

    @Autowired
    private InspectionActionService inspectionActionService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;
}
