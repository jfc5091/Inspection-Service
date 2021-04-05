package com.firerms.entity.inspections;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "INSPECTION_ACTION")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class InspectionAction implements TenantSupport {

    @Id
    @Column(name = "INSPECTION_ACTION_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectionActionId;
    @Column(name ="INSPECTION_ID")
    private Long inspectionId;
    @Column(name = "ACTION")
    private String action;
    @Column(name = "DATE")
    private Date date;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "NARRATIVE")
    private String narrative;
    @Column(name = "FDID")
    private Long fdid;

    public InspectionAction() {
    }

    public InspectionAction(Long inspectionActionId, Long inspectionId, String action, Date date,
                            String description, String narrative, Long fdid) {
        this.inspectionActionId = inspectionActionId;
        this.inspectionId = inspectionId;
        this.action = action;
        this.date = date;
        this.description = description;
        this.narrative = narrative;
        this.fdid = fdid;
    }

    public Long getInspectionActionId() {
        return inspectionActionId;
    }

    public Long getInspectionId() {
        return inspectionId;
    }

    public String getAction() {
        return action;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getNarrative() {
        return narrative;
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
