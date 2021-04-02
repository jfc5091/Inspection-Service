package com.firerms.multiTenancy.auditLogs.entity;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "FDID")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class Fdid implements TenantSupport {

    @Id
    @Column(name = "FDID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long fdid;
    @Column(name = "DEPARTMENT")
    private String department;
    @Column(name = "CITY")
    private String city;
    @Column(name = "STATE")
    private String state;

    public Fdid() {
    }

    public Fdid(Long fdid, String department, String city, String state) {
        this.fdid = fdid;
        this.department = department;
        this.city = city;
        this.state = state;
    }

    public Long getFdid() {
        return fdid;
    }

    public String getDepartment() {
        return department;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public void setFdid(Long fdid) {
        this.fdid = fdid;
    }

    @Override
    public void setTenantId(String fdid) {
        this.setFdid(Long.valueOf(fdid));
    }
}
