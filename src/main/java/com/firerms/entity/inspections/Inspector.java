package com.firerms.entity.inspections;

import com.firerms.multiTenancy.TenantSupport;
import com.firerms.security.entity.User;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;


@Entity
@Table(name = "INSPECTOR")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class Inspector implements TenantSupport {

    @Id
    @Column(name = "INSPECTOR_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long inspectorId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="USER_ID")
    private User usersId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "FDID")
    private Long fdid;

    public Inspector() {
    }

    public Inspector(Long inspectorId, User usersId, String firstName, String lastName, String phone, Long fdid) {
        this.inspectorId = inspectorId;
        this.usersId = usersId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.fdid = fdid;
    }

    public Long getInspectorId() {
        return inspectorId;
    }

    public User getUsersId() {
        return usersId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
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
