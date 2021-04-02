package com.firerms.entity.checklists;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "INSPECTION_VIOLTAION_STATUS")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionViolationStatus implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_VIOLTAION_STATUS_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionViolationStatusId;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionViolationStatus() {
    }

    public InspectionViolationStatus(Long inspectionViolationStatusId, String status, Long fdid) {
        this.inspectionViolationStatusId = inspectionViolationStatusId;
        this.status = status;
        this.fdid = fdid;
    }

    public Long getInspectionViolationStatusId() {
        return inspectionViolationStatusId;
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
