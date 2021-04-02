package com.firerms.response;

import com.firerms.entity.inspections.Inspection;

import java.util.List;

public class AllInspectionsResponse {

    private List<Inspection> inspectionList;

    public AllInspectionsResponse(List<Inspection> inspectionList) {
        this.inspectionList = inspectionList;
    }

    public List<Inspection> getInspectionList() {
        return inspectionList;
    }
}
