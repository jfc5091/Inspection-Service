package com.firerms.entity.inspections;

import com.firerms.entity.checklists.InspectionViolation;
import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "INSPECTOR_ID")
    private Inspector inspector;
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
    @OneToMany(
            mappedBy = "inspectionId",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<InspectionAction> inspectionActionList = new ArrayList<>();
    @OneToMany(
            mappedBy = "inspectionId",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<InspectionViolation> inspectionViolationList = new ArrayList<>();
    @Column(name = "FDID")
    private Long fdid;

    public Inspection() {
    }

    public Inspection(Long inspectionId, Long propertyId, Inspector inspector,
                      Long inspectionChecklistId, String status, String narrative,
                      String occupantSignatureUrl, String inspectorSignatureUrl, Long fdid) {
        this.inspectionId = inspectionId;
        this.propertyId = propertyId;
        this.inspector = inspector;
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

    public Inspector getInspector() {
        return inspector;
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

    public List<InspectionAction> getInspectionActionList() {
        return inspectionActionList;
    }

    public List<InspectionViolation> getInspectionViolationList() {
        return inspectionViolationList;
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

    public void setOccupantSignatureUrl(String occupantSignatureUrl) {
        this.occupantSignatureUrl = occupantSignatureUrl;
    }

    public void setInspectorSignatureUrl(String inspectorSignatureUrl) {
        this.inspectorSignatureUrl = inspectorSignatureUrl;
    }

    public void setInspector(Inspector inspector) {
        this.inspector = inspector;
    }
}
