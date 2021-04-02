package com.firerms.multiTenancy.unit;

import com.firerms.multiTenancy.RequestInterceptor;
import com.firerms.multiTenancy.TenantContext;
import com.firerms.multiTenancy.auditLogs.CachedServletInputStream;
import com.firerms.multiTenancy.auditLogs.entity.AuditLogs;
import com.firerms.multiTenancy.auditLogs.respository.AuditLogsRepository;
import com.firerms.security.entity.User;
import com.firerms.security.entity.UsersAuthorities;
import com.firerms.security.repository.UserRepository;
import com.firerms.security.util.JwtUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest
class RequestInterceptorUnitTest {

    @Autowired
    private RequestInterceptor requestInterceptor;

    @MockBean
    private JwtUtility jwtUtility;

    @MockBean
    private AuditLogsRepository auditLogsRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void preHandleTest() throws IOException {
        UsersAuthorities usersAuthorities = new UsersAuthorities(1L, "ADMIN", "ADMIN");
        User user = new User(1L, "username", "password12", "email@example.com",
                usersAuthorities, true, 1L);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        String token = "token";
        String authorizationHeader = "Bearer " + token;
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object object = Mockito.mock(Object.class);
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/inspection/1");
        when(request.getInputStream()).thenReturn(new CachedServletInputStream(new ByteArrayOutputStream()));
        when(jwtUtility.extractUsername(token)).thenReturn(user.getUsername());
        when(jwtUtility.extractFdid(token)).thenReturn(user.getFdid());
        when(userRepository.findAllByUsername(user.getUsername())).thenReturn(userList);

        boolean preHandleResponse = requestInterceptor.preHandle(request, response, object);

        assertTrue(preHandleResponse);
        assertEquals(Long.valueOf(TenantContext.getCurrentTenant()), user.getFdid());
    }

    @Test
    void preHandleNoAuthorizationHeaderTest() throws IOException {
        String tenantId = "1";
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object object = Mockito.mock(Object.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-TenantID")).thenReturn(tenantId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/inspection/1");
        when(request.getInputStream()).thenReturn(new CachedServletInputStream(new ByteArrayOutputStream()));

        boolean preHandleResponse = requestInterceptor.preHandle(request, response, object);

        assertTrue(preHandleResponse);
        assertEquals(TenantContext.getCurrentTenant(), tenantId);
    }

    @Test
    void preHandleTenantIdNullTest() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object object = Mockito.mock(Object.class);

        boolean preHandleResponse = requestInterceptor.preHandle(request, response, object);

        assertFalse(preHandleResponse);
    }

    @Test
    void preHandleNoRequestMethodTest() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object object = Mockito.mock(Object.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-TenantID")).thenReturn("1");
        when(request.getMethod()).thenReturn("GET");

        boolean preHandleResponse = requestInterceptor.preHandle(request, response, object);

        assertFalse(preHandleResponse);
    }

    @Test
    void preHandleNoRequestUriTest() throws IOException {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object object = Mockito.mock(Object.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-TenantID")).thenReturn("1");

        boolean preHandleResponse = requestInterceptor.preHandle(request, response, object);

        assertFalse(preHandleResponse);
    }

    @Test
    void preHandleUserDoesNotExistTest() throws IOException {
        UsersAuthorities usersAuthorities = new UsersAuthorities(1L, "ADMIN", "ADMIN");
        User user = new User(1L, "username", "password12", "email@example.com",
                usersAuthorities, true, 1L);
        String token = "token";
        String authorizationHeader = "Bearer " + token;
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object object = Mockito.mock(Object.class);
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/inspection/1");
        when(request.getInputStream()).thenReturn(new CachedServletInputStream(new ByteArrayOutputStream()));
        when(jwtUtility.extractUsername(token)).thenReturn(user.getUsername());
        when(jwtUtility.extractFdid(token)).thenReturn(user.getFdid());

        boolean preHandleResponse = requestInterceptor.preHandle(request, response, object);

        assertFalse(preHandleResponse);
    }

    @Test
    void postHandle() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ContentCachingResponseWrapper response = Mockito.mock(ContentCachingResponseWrapper.class);
        Object object = Mockito.mock(Object.class);
        ModelAndView modelAndView = Mockito.mock(ModelAndView.class);
        AuditLogs auditLogs = Mockito.mock(AuditLogs.class);
        ReflectionTestUtils.setField(requestInterceptor, "auditLogs", auditLogs);
        when(response.getContentAsByteArray()).thenReturn(new byte[1]);

        requestInterceptor.postHandle(request, response, object, modelAndView);

        assertNull(TenantContext.getCurrentTenant());
    }
}