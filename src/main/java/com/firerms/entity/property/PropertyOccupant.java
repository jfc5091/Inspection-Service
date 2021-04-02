package com.firerms.entity.property;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "PROPERTY_OCCUPANT")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class PropertyOccupant implements TenantSupport {

    @Id
    @Column(name = "PROPERTY_OCCUPANT_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long propertyOccupantId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "FAX")
    private String fax;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="ADDRESS_ID")
    private Address addressId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="PROPERTY_ID")
    private Property propertyId;
    @Column(name = "FDID")
    private Long fdid;

    public PropertyOccupant() {
    }

    public PropertyOccupant(Long propertyOccupantId, String firstName, String lastName, String phone, String fax,
                            Address addressId, Property propertyId, Long fdid) {
        this.propertyOccupantId = propertyOccupantId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.fax = fax;
        this.addressId = addressId;
        this.propertyId = propertyId;
        this.fdid = fdid;
    }

    public Long getPropertyOccupantId() {
        return propertyOccupantId;
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

    public String getFax() {
        return fax;
    }

    public Address getAddressId() {
        return addressId;
    }

    public Property getPropertyId() {
        return propertyId;
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
