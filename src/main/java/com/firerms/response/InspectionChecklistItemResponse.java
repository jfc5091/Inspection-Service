package com.firerms.response;

import com.firerms.entity.checklists.InspectionChecklistItem;

public class InspectionChecklistItemResponse {

    private InspectionChecklistItem inspectionChecklistItem;

    public InspectionChecklistItemResponse(InspectionChecklistItem inspectionChecklistItem) {
        this.inspectionChecklistItem = inspectionChecklistItem;
    }

    public InspectionChecklistItem getInspectionChecklistItem() {
        return inspectionChecklistItem;
    }
}
