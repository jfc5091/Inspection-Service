package com.firerms.response;

import com.firerms.entity.inspections.Inspection;

public class InspectionResponse {

    private Inspection inspection;

    public InspectionResponse(Inspection inspection) {
        this.inspection = inspection;
    }

    public Inspection getInspection() {
        return inspection;
    }
}
