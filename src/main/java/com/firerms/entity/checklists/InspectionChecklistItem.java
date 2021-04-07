package com.firerms.entity.checklists;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "INSPECTION_CHECKLIST_ITEM")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionChecklistItem implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_CHECKLIST_ITEM_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionChecklistItemId;
    @Column(name ="INSPECTION_CHECKLIST_ID")
    private Long inspectionChecklistId;
    @Column(name ="FIRE_CODE_ID")
    private Long fireCodeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="INSPECTION_CHECKLIST_ITEM_STATUS_ID")
    private InspectionChecklistItemStatus inspectionChecklistItemStatusId;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionChecklistItem() {
    }

    public InspectionChecklistItem(Long inspectionChecklistItemId, Long inspectionChecklistId,
                                   Long fireCodeId, InspectionChecklistItemStatus inspectionChecklistItemStatusId, String description,
                                   Long fdid) {
        this.inspectionChecklistItemId = inspectionChecklistItemId;
        this.inspectionChecklistId = inspectionChecklistId;
        this.fireCodeId = fireCodeId;
        this.inspectionChecklistItemStatusId = inspectionChecklistItemStatusId;
        this.description = description;
        this.fdid = fdid;
    }

    public Long getInspectionChecklistItemId() {
        return inspectionChecklistItemId;
    }

    public Long getInspectionChecklistId() {
        return inspectionChecklistId;
    }

    public Long getFireCodeId() {
        return fireCodeId;
    }

    public InspectionChecklistItemStatus getInspectionChecklistItemStatusId() {
        return inspectionChecklistItemStatusId;
    }

    public String getDescription() {
        return description;
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
