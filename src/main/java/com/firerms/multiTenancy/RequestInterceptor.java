package com.firerms.multiTenancy;

import com.firerms.multiTenancy.auditLogs.MultiReadHttpServletRequest;
import com.firerms.multiTenancy.auditLogs.entity.AuditLogs;
import com.firerms.multiTenancy.auditLogs.respository.AuditLogsRepository;
import com.firerms.security.entity.User;
import com.firerms.security.repository.UserRepository;
import com.firerms.security.util.JwtUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private AuditLogsRepository auditLogsRepository;

    @Autowired
    private UserRepository userRepository;

    private AuditLogs auditLogs;

    private static final Logger LOG = LoggerFactory.getLogger(RequestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) throws IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String tenantID = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            username = jwtUtility.extractUsername(token);
            tenantID = jwtUtility.extractFdid(token).toString();
        }
        else if (authorizationHeader == null) {
            tenantID = request.getHeader("X-TenantID");
        }
        if (tenantID == null) {
            LOG.warn("Inspection Service - FDID in Token or X-TenantID not present in the Request Header");
            response.setStatus(400);
            return false;
        }
        else if (request.getMethod() == null || request.getRequestURI() == null) {
            LOG.warn("Inspection Service - null values in the Request Header");
            response.setStatus(400);
            return false;
        }

        TenantContext.setCurrentTenant(tenantID);

        User user = null;
        if (username != null) {
            List<User> userList = userRepository.findAllByUsername(username);
            for (User u : userList) {
                if (u.getFdid().equals(Long.valueOf(tenantID))) {
                    user = u;
                    break;
                }
            }
            if (user == null) {
                LOG.warn("Inspection Service - user from Request Header is null");
                response.setStatus(400);
                return false;
            }
        }

        MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(request);

        auditLogs = new AuditLogs(multiReadRequest.getBody(),
                multiReadRequest.getMethod(), multiReadRequest.getRequestURI(), user, 0, null, new Date(),
                Long.valueOf(tenantID));

        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        auditLogs.setResponseStatus(response.getStatus());
        ContentCachingResponseWrapper responseCopier = new ContentCachingResponseWrapper(response);
        byte[] responseByteArray = responseCopier.getContentAsByteArray();
        auditLogs.setResponse(new String(responseByteArray, StandardCharsets.UTF_8));
        auditLogsRepository.save(auditLogs);
        TenantContext.clear();
    }
}
