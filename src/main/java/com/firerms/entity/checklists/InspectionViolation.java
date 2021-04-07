package com.firerms.entity.checklists;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "INSPECTION_VIOLATION")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionViolation implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_VIOLATION_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionViolationId;
    @Column(name ="FIRE_CODE_ID")
    private Long fireCodeId;
    @Column(name ="INSPECTION_VIOLATION_STATUS_ID")
    private Long inspectionViolationStatusId;
    @Column(name ="INSPECTION_ID")
    private Long inspectionId;
    @Column(name ="INSPECTION_CHECKLIST_ITEM_ID")
    private Long inspectionChecklistItemId;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "LOCATION")
    private String location;
    @Column(name = "NARRATIVE")
    private String narrative;
    @Column(name = "DATE_FOUND")
    private Date dateFound;
    @Column(name = "ABATE_DATE")
    private Date abateDate;
    @Column(name = "DATE_CORRECTED")
    private Date dateCorrected;
    @OneToMany(
            mappedBy = "inspectionViolationId",
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private final List<InspectionViolationImageUrl> inspectionViolationImageUrlList = new ArrayList<>();
    @Column(name = "FDID")
    private Long fdid;

    public InspectionViolation() {
    }

    public InspectionViolation(Long inspectionViolationId, Long fireCodeId, Long inspectionViolationStatusId,
                               Long inspectionId, Long inspectionChecklistItemId, String description, String location,
                               String narrative, Date dateFound, Date abateDate, Date dateCorrected, Long fdid) {
        this.inspectionViolationId = inspectionViolationId;
        this.fireCodeId = fireCodeId;
        this.inspectionViolationStatusId = inspectionViolationStatusId;
        this.inspectionId = inspectionId;
        this.inspectionChecklistItemId = inspectionChecklistItemId;
        this.description = description;
        this.location = location;
        this.narrative = narrative;
        this.dateFound = dateFound;
        this.abateDate = abateDate;
        this.dateCorrected = dateCorrected;
        this.fdid = fdid;
    }

    public Long getInspectionViolationId() {
        return inspectionViolationId;
    }

    public Long getFireCodeId() {
        return fireCodeId;
    }

    public Long getInspectionViolationStatusId() {
        return inspectionViolationStatusId;
    }

    public Long getInspectionId() {
        return inspectionId;
    }

    public Long getInspectionChecklistItemId() {
        return inspectionChecklistItemId;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getNarrative() {
        return narrative;
    }

    public Date getDateFound() {
        return dateFound;
    }

    public Date getAbateDate() {
        return abateDate;
    }

    public Date getDateCorrected() {
        return dateCorrected;
    }

    public Long getFdid() {
        return fdid;
    }

    public List<InspectionViolationImageUrl> getInspectionViolationImageUrlList() {
        return inspectionViolationImageUrlList;
    }

    public void setFdid(Long fdid) {
        this.fdid = fdid;
    }

    @Override
    public void setTenantId(String fdid) {
        this.fdid = Long.valueOf(fdid);
    }

    public void addInspectionViolationImageUrl(InspectionViolationImageUrl inspectionViolationImageUrl) {
        if (!this.inspectionViolationImageUrlList.contains(inspectionViolationImageUrl)) {
            this.inspectionViolationImageUrlList.add(inspectionViolationImageUrl);
        }
    }

    public void deleteInspectionViolationImageUrl(InspectionViolationImageUrl inspectionViolationImageUrl) {
        this.inspectionViolationImageUrlList.remove(inspectionViolationImageUrl);
    }
}
