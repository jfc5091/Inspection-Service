package com.firerms.entity.checklists;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "FIRE_CODE")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class FireCode implements TenantSupport {

    @Id
    @Column(name = "FIRE_CODE_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long fireCodeId;
    @Column(name = "CODE")
    private String code;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ENABLED")
    private boolean enabled;
    @Column(name = "FDID")
    private Long fdid;

    public FireCode() {
    }

    public FireCode(Long fireCodeId, String code, String description, boolean enabled, Long fdid) {
        this.fireCodeId = fireCodeId;
        this.code = code;
        this.description = description;
        this.enabled = enabled;
        this.fdid = fdid;
    }

    public Long getFireCodeId() {
        return fireCodeId;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
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
