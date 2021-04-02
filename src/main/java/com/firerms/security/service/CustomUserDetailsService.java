package com.firerms.security.service;

import com.firerms.security.entity.User;
import com.firerms.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private static final String NOT_FOUND_ERROR_MSG = "%s not found with username: %s";

    @Override
    public UserDetails loadUserByUsername(String usernameAndFdid) {
        String[] arrUsernameAndFdid = usernameAndFdid.split("#");
        String username = arrUsernameAndFdid[0];
        String fdid = arrUsernameAndFdid[1];
        User user = userRepository.findByUsernameAndFdid(username, Long.valueOf(fdid));
        if (user == null || user.isDisabled()) {
            String errorMessage = String.format(NOT_FOUND_ERROR_MSG, "User", username);
            LOG.error("Inspection Service - loadUserByUsername request: {}", errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String[] authorities = {user.getUsersAuthorities().getInspectionAuthority()};
        return AuthorityUtils.createAuthorityList(authorities);
    }
}
