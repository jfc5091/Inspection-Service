package com.firerms.request;

import com.firerms.entity.checklists.InspectionChecklist;

public class InspectionChecklistRequest {

    private InspectionChecklist inspectionChecklist;

    public InspectionChecklistRequest() {
    }

    public InspectionChecklistRequest(InspectionChecklist inspectionChecklist) {
        this.inspectionChecklist = inspectionChecklist;
    }

    public InspectionChecklist getInspectionChecklist() {
        return inspectionChecklist;
    }
}
