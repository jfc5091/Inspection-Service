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
public class InspectionViolationImage implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_VIOLATION_IMAGE_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionViolationImageId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="INSPECTION_VIOLATION_ID")
    private InspectionViolation inspectionViolationId;
    @Column(name = "IMAGE_URL")
    private String imageUrl;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionViolationImage() {
    }

    public InspectionViolationImage(Long inspectionViolationImageId, InspectionViolation inspectionViolationId,
                                    String imageUrl, Long fdid) {
        this.inspectionViolationImageId = inspectionViolationImageId;
        this.inspectionViolationId = inspectionViolationId;
        this.imageUrl = imageUrl;
        this.fdid = fdid;
    }

    public Long getInspectionViolationImageId() {
        return inspectionViolationImageId;
    }

    public InspectionViolation getInspectionViolationId() {
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
