package com.firerms.request;

import com.firerms.entity.inspections.InspectionAction;

public class InspectionActionRequest {

    private InspectionAction inspectionAction;

    public InspectionActionRequest() {
    }

    public InspectionActionRequest(InspectionAction inspectionAction) {
        this.inspectionAction = inspectionAction;
    }

    public InspectionAction getInspectionAction() {
        return inspectionAction;
    }
}
