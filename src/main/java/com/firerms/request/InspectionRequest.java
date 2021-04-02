package com.firerms.request;

import com.firerms.entity.inspections.Inspection;

public class InspectionRequest {

    private Inspection inspection;

    public InspectionRequest() {
    }

    public InspectionRequest(Inspection inspection) {
        this.inspection = inspection;
    }

    public Inspection getInspection() {
        return inspection;
    }
}
