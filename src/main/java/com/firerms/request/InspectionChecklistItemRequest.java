package com.firerms.request;

import com.firerms.entity.checklists.InspectionChecklistItem;

public class InspectionChecklistItemRequest {

    private InspectionChecklistItem inspectionChecklistItem;

    public InspectionChecklistItemRequest() {
    }

    public InspectionChecklistItemRequest(InspectionChecklistItem inspectionChecklistItem) {
        this.inspectionChecklistItem = inspectionChecklistItem;
    }

    public InspectionChecklistItem getInspectionChecklistItem() {
        return inspectionChecklistItem;
    }
}
