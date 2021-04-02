package com.firerms.response;

import com.firerms.entity.checklists.InspectionChecklist;

public class InspectionChecklistResponse {

    private InspectionChecklist inspectionChecklist;

    public InspectionChecklistResponse(InspectionChecklist inspectionChecklist) {
        this.inspectionChecklist = inspectionChecklist;
    }

    public InspectionChecklist getInspectionChecklist() {
        return inspectionChecklist;
    }
}
