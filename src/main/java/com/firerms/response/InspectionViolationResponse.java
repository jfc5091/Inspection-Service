package com.firerms.response;

import com.firerms.entity.checklists.InspectionViolation;

public class InspectionViolationResponse {

    private InspectionViolation inspectionViolation;

    public InspectionViolationResponse(InspectionViolation inspectionViolation) {
        this.inspectionViolation = inspectionViolation;
    }

    public InspectionViolation getInspectionViolation() {
        return inspectionViolation;
    }
}
