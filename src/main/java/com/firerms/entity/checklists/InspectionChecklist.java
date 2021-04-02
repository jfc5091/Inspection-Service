package com.firerms.entity.checklists;

import com.firerms.entity.property.Property;
import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "INSPECTION_CHECKLIST")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionChecklist implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_CHECKLIST_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionChecklistId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="INSPECTION_ID")
    private Property inspectionId;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "ENABLED")
    private boolean enabled;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionChecklist() {
    }

    public InspectionChecklist(Long inspectionChecklistId, Property inspectionId, String type, boolean enabled,
                               Long fdid) {
        this.inspectionChecklistId = inspectionChecklistId;
        this.inspectionId = inspectionId;
        this.type = type;
        this.enabled = enabled;
        this.fdid = fdid;
    }

    public Long getInspectionChecklistId() {
        return inspectionChecklistId;
    }

    public Property getInspectionId() {
        return inspectionId;
    }

    public String getType() {
        return type;
    }

    public boolean isEnabled() {
        return enabled;
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
