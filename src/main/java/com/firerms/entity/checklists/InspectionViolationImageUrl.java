package com.firerms.entity.checklists;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "INSPECTION_VIOLATION_IMAGE")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionViolationImageUrl implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_VIOLATION_IMAGE_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionViolationImageId;
    @Column(name ="INSPECTION_VIOLATION_ID")
    private Long inspectionViolationId;
    @Column(name = "IMAGE_URL")
    private String imageUrl;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionViolationImageUrl() {
    }

    public InspectionViolationImageUrl(Long inspectionViolationId,
                                       String imageUrl, Long fdid) {
        this.inspectionViolationImageId = null;
        this.inspectionViolationId = inspectionViolationId;
        this.imageUrl = imageUrl;
        this.fdid = fdid;
    }

    public InspectionViolationImageUrl(Long inspectionViolationImageId, Long inspectionViolationId,
                                       String imageUrl, Long fdid) {
        this.inspectionViolationImageId = inspectionViolationImageId;
        this.inspectionViolationId = inspectionViolationId;
        this.imageUrl = imageUrl;
        this.fdid = fdid;
    }

    public Long getInspectionViolationImageId() {
        return inspectionViolationImageId;
    }

    public Long getInspectionViolationId() {
        return inspectionViolationId;
    }

    public String getImageUrl() {
        return imageUrl;
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
