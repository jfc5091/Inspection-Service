package com.firerms.entity.property;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Entity
@Table(name = "ADDRESS")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class Address implements TenantSupport {

    @Id
    @Column(name = "ADDRESS_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long addressId;
    @Column(name = "ADDRESS_LINE1")
    private String addressLine1;
    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;
    @Column(name = "CITY")
    private String city;
    @Column(name = "STATE")
    private String state;
    @Column(name = "ZIP")
    private String zip;
    @Column(name = "LONGITUDE")
    private double longitude;
    @Column(name = "LATITUDE")
    private double latitude;
    @Column(name ="ADDRESS_TYPE_ID")
    private Long addressTypeId;
    @Column(name = "ENABLED")
    private boolean enabled;
    @Column(name = "FDID")
    private Long fdid;

    public Address() {
    }

    public Address(Long addressId, String addressLine1, String addressLine2, String city, String state, String zip,
                   double longitude, double latitude, Long addressTypeId, boolean enabled, Long fdid) {
        this.addressId = addressId;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.longitude = longitude;
        this.latitude = latitude;
        this.addressTypeId = addressTypeId;
        this.enabled = enabled;
        this.fdid = fdid;
    }

    public Long getAddressId() {
        return addressId;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public Long getAddressTypeId() {
        return addressTypeId;
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
