package com.firerms.multiTenancy.auditLogs.entity;

import com.firerms.multiTenancy.TenantSupport;
import com.firerms.security.entity.User;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "AUDIT_LOGS")
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "FDID", type = "string")})
@Filter(name = "tenantFilter", condition = "FDID = :FDID")
public class AuditLogs implements TenantSupport {

    @Id
    @Column(name = "AUDIT_LOGS_ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long auditLogsId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="USERS_ID")
    private User usersId;
    @Column(name = "REQUEST")
    private String request;
    @Column(name = "REQUEST_TYPE")
    private String requestType;
    @Column(name = "REQUEST_URI")
    private String requestUri;
    @Column(name = "RESPONSE_STATUS")
    private int responseStatus;
    @Column(name = "RESPONSE")
    private String response;
    @Column(name = "DATE")
    private Date date;
    @Column(name = "FDID")
    private Long fdid;

    public AuditLogs() {
    }

    public AuditLogs(String request, String requestType, String requestUri, User usersId, int responseStatus,
                     String response, Date date, Long fdid) {
        this.auditLogsId = null;
        this.usersId = usersId;
        this.request = request;
        this.requestType = requestType;
        this.requestUri = requestUri;
        this.responseStatus = responseStatus;
        this.response = response;
        this.date = date;
        this.fdid = fdid;
    }

    public Long getFdid() {
        return fdid;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setFdid(Long fdid) {
        this.fdid = fdid;
    }

    @Override
    public void setTenantId(String fdid) {
        this.fdid = Long.valueOf(fdid);
    }
}

