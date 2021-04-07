package com.firerms.entity.inspections;

import com.firerms.entity.checklists.InspectionChecklist;
import com.firerms.entity.property.Property;
import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "INSPECTION")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class Inspection implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionId;
    @Column(name ="PROPERTY_ID")
    private Long propertyId;
    @Column(name ="INSPECTOR_ID")
    private Long inspectorId;
    @Column(name ="INSPECTION_CHECKLIST_ID")
    private Long inspectionChecklistId;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "NARRATIVE")
    private String narrative;
    @Column(name = "OCCUPANT_SIGNATURE_URL")
    private String occupantSignatureUrl;
    @Column(name = "INSPECTOR_SIGNATURE_URL")
    private String inspectorSignatureUrl;
    @Column(name = "FDID")
    private Long fdid;

    public Inspection() {
    }

    public Inspection(Long inspectionId, Long propertyId, Long inspectorId,
                      Long inspectionChecklistId, String status, String narrative,
                      String occupantSignatureUrl, String inspectorSignatureUrl, Long fdid) {
        this.inspectionId = inspectionId;
        this.propertyId = propertyId;
        this.inspectorId = inspectorId;
        this.inspectionChecklistId = inspectionChecklistId;
        this.status = status;
        this.narrative = narrative;
        this.occupantSignatureUrl = occupantSignatureUrl;
        this.inspectorSignatureUrl = inspectorSignatureUrl;
        this.fdid = fdid;
    }

    public Long getInspectionId() {
        return inspectionId;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public Long getInspectorId() {
        return inspectorId;
    }

    public Long getInspectionChecklistId() {
        return inspectionChecklistId;
    }

    public String getStatus() {
        return status;
    }

    public String getNarrative() {
        return narrative;
    }

    public String getOccupantSignatureUrl() {
        return occupantSignatureUrl;
    }

    public String getInspectorSignatureUrl() {
        return inspectorSignatureUrl;
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
