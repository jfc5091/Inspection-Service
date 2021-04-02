package com.firerms.response;

import com.firerms.entity.inspections.InspectionAction;

public class InspectionActionResponse {

    private InspectionAction inspectionAction;

    public InspectionActionResponse(InspectionAction inspectionAction) {
        this.inspectionAction = inspectionAction;
    }

    public InspectionAction getInspectionAction() {
        return inspectionAction;
    }
}
