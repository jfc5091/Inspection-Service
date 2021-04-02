package com.firerms.security.entity;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "USERS_AUTHORITIES")
public class UsersAuthorities {

    @Id
    @Column(name = "AUTHORITY_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long authorityId;
    @Column(name = "INSPECTION_AUTHORITY")
    @Pattern(regexp = "^[a-zA-Z_]*$", message = "inspection authority contains illegal characters")
    @Size(max = 20, message = "inspection authority is too long")
    private String inspectionAuthority;
    @Column(name = "LOGIN_AUTHORITY")
    @Pattern(regexp = "^[a-zA-Z_]*$", message = "login authority contains illegal characters")
    @Size(max = 20, message = "login authority is too long")
    private String loginAuthority;

    public UsersAuthorities() {
    }

    public UsersAuthorities(Long authorityId, String inspectionAuthority, String loginAuthority) {
        this.authorityId = authorityId;
        this.inspectionAuthority = inspectionAuthority;
        this.loginAuthority = loginAuthority;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public String getInspectionAuthority() {
        return inspectionAuthority;
    }

    public String getLoginAuthority() {
        return loginAuthority;
    }
}
