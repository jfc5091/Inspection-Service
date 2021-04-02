package com.firerms.security.filter;

import com.firerms.multiTenancy.auditLogs.MultiReadHttpServletRequest;
import com.firerms.security.service.CustomUserDetailsService;
import com.firerms.security.util.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        String token = null;
        String username = null;
        String fdid = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            username = jwtUtility.extractUsername(token);
            Object extractedFdid = jwtUtility.extractFdid(token);
            if (extractedFdid != null) {
                fdid = extractedFdid.toString();
            }        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String usernameAndFdid = username + "#" + fdid;
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameAndFdid);
            if (Boolean.TRUE.equals(jwtUtility.validateToken(token, userDetails))) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(httpServletRequest);
        ContentCachingResponseWrapper responseCopier = new ContentCachingResponseWrapper(httpServletResponse);

        filterChain.doFilter(multiReadRequest, responseCopier);

        responseCopier.copyBodyToResponse();
    }
}
