package com.firerms.entity.checklists;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "INSPECTION_CHECKLIST_ITEM_STATUS")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionChecklistItemStatus implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_CHECKLIST_ITEM_STATUS_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionChecklistItemStatusId;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionChecklistItemStatus() {
    }

    public InspectionChecklistItemStatus(Long inspectionChecklistItemStatusId, String status, Long fdid) {
        this.inspectionChecklistItemStatusId = inspectionChecklistItemStatusId;
        this.status = status;
        this.fdid = fdid;
    }

    public Long getInspectionChecklistItemStatusId() {
        return inspectionChecklistItemStatusId;
    }

    public String getStatus() {
        return status;
    }

    public Long getFdid() {
        return fdid;
    }

    public void setFdid(Long fdid) {
        this.fdid = fdid;
    }

    @Override
    public void setTenantId(String fdid) {
        this.fdid = Long.valueOf(fdid);
    }
}
