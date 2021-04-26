package com.firerms.security.config;

import com.firerms.security.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtFilter jwtFilter;

    public static final String USER_AUTHORITY = "USER";
    public static final String ADMIN_AUTHORITY = "ADMIN";
    public static final String SUPER_ADMIN_AUTHORITY = "SUPER_ADMIN";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();
        http.csrf().disable().authorizeRequests()
                .antMatchers("/inspection/create").hasAnyAuthority(ADMIN_AUTHORITY, SUPER_ADMIN_AUTHORITY)
                .antMatchers(HttpMethod.GET, "/inspection/{\\d+}").hasAnyAuthority(USER_AUTHORITY, ADMIN_AUTHORITY, SUPER_ADMIN_AUTHORITY)
                .antMatchers("/inspection/update").hasAnyAuthority(ADMIN_AUTHORITY, SUPER_ADMIN_AUTHORITY)
                .antMatchers(HttpMethod.DELETE, "/inspection/{\\d+}").hasAuthority(SUPER_ADMIN_AUTHORITY)
                .antMatchers("/inspection/all").hasAnyAuthority(USER_AUTHORITY, ADMIN_AUTHORITY, SUPER_ADMIN_AUTHORITY)
                .anyRequest().authenticated()
                .and().exceptionHandling().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
