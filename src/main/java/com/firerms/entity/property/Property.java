package com.firerms.entity.property;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "PROPERTY")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class Property implements TenantSupport {

    @Id
    @Column(name = "PROPERTY_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long propertyId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="ADDRESS_ID")
    private Address addressId;
    @Column(name = "FDID")
    private Long fdid;

    public Property() {
    }

    public Property(Long propertyId, Address addressId, Long fdid) {
        this.propertyId = propertyId;
        this.addressId = addressId;
        this.fdid = fdid;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public Address getAddressId() {
        return addressId;
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
