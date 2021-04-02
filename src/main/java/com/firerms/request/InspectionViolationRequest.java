package com.firerms.request;

import com.firerms.entity.checklists.InspectionViolation;

public class InspectionViolationRequest {

    private InspectionViolation inspectionViolation;

    public InspectionViolationRequest() {
    }

    public InspectionViolationRequest(InspectionViolation inspectionViolation) {
        this.inspectionViolation = inspectionViolation;
    }

    public InspectionViolation getInspectionViolation() {
        return inspectionViolation;
    }
}
