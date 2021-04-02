package com.firerms.controller;

import com.firerms.exception.InspectionExceptionHandler;
import com.firerms.service.InspectionChecklistItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inspection/checkitem")
public class InspectionChecklistItemController {

    @Autowired
    private InspectionChecklistItemService inspectionChecklistItemService;

    @Autowired
    private InspectionExceptionHandler inspectionExceptionHandler;
}
