package com.firerms.entity.property;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "ADDRESS_TYPE")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class AddressType implements TenantSupport {

    @Id
    @Column(name = "ADDRESS_TYPE_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long addressTypeId;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "ENABLED")
    private boolean enabled;
    @Column(name = "FDID")
    private Long fdid;

    public AddressType() {
    }

    public AddressType(Long addressTypeId, String type, boolean enabled, Long fdid) {
        this.addressTypeId = addressTypeId;
        this.type = type;
        this.enabled = enabled;
        this.fdid = fdid;
    }

    public Long getAddressTypeId() {
        return addressTypeId;
    }

    public String getType() {
        return type;
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
