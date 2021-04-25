package com.firerms.entity.inspections;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "INSPECTION_IMAGE")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionImageUrl implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_IMAGE_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionImageId;
    @Column(name ="INSPECTION_ID")
    private Long inspectionId;
    @Column(name = "IMAGE_URL")
    private String imageUrl;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionImageUrl() {
    }

    public InspectionImageUrl(Long inspectionId,
                                       String imageUrl, Long fdid) {
        this.inspectionImageId = null;
        this.inspectionId = inspectionId;
        this.imageUrl = imageUrl;
        this.fdid = fdid;
    }

    public InspectionImageUrl(Long inspectionImageId, Long inspectionId,
                                       String imageUrl, Long fdid) {
        this.inspectionImageId = inspectionImageId;
        this.inspectionId = inspectionId;
        this.imageUrl = imageUrl;
        this.fdid = fdid;
    }

    public Long getInspectionImageId() {
        return inspectionImageId;
    }

    public Long getInspectionId() {
        return inspectionId;
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
