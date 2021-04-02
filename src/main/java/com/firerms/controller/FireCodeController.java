package com.firerms.controller;

import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.FireCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspection/firecode")
public class FireCodeController {

    @Autowired
    private FireCodeService fireCodeService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;
}
