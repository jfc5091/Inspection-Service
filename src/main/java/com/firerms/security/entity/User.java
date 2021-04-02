package com.firerms.security.entity;

import com.firerms.multiTenancy.TenantSupport;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "USERS")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class User implements TenantSupport {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "USERNAME")
    @Pattern(regexp = "^[A-Za-z0-9]*$", message = "user username contains illegal characters")
    @NotNull(message = "user username cannot be null")
    private String username;
    @Column(name = "PASSWORD")
    @NotNull(message = "user password cannot be null")
    @Size(min = 10, message = "user password is too short")
    private String password;
    @Column(name = "ENABLED")
    private boolean enabled;
    @Column(name = "EMAIL")
    @Email(message = "user email is not valid")
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name ="USERS_AUTHORITIES_ID")
    private UsersAuthorities usersAuthorities;
    @Column(name = "FDID")
    @Positive(message = "user FDID should be a positive number")
    private Long fdid;

    public User() {
    }

    public User(String username, String password, String email, UsersAuthorities usersAuthorities, Boolean enabled, Long fdid) {
        this.userId = null;
        this.username = username;
        this.password = password.replaceAll("\\s+","");
        this.email = email;
        this.usersAuthorities = usersAuthorities;
        this.enabled = enabled;
        this.fdid = fdid;
    }

    public User(Long userId, String username, String password, String email, UsersAuthorities usersAuthorities, Boolean enabled, Long fdid) {
        this.userId = userId;
        this.username = username;
        this.password = password.replaceAll("\\s+","");
        this.email = email;
        this.usersAuthorities = usersAuthorities;
        this.enabled = enabled;
        this.fdid = fdid;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public UsersAuthorities getUsersAuthorities() {
        return usersAuthorities;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public Long getFdid() {
        return fdid;
    }

    public void setPassword(String password) {
        this.password = password.replaceAll("\\s+","");
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFdid(Long fdid) {
        this.fdid = fdid;
    }

    @Override
    public void setTenantId(String fdid) {
        this.setFdid(Long.valueOf(fdid));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
