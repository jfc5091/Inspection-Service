package com.firerms.security.service.unit;

import com.firerms.security.entity.User;
import com.firerms.security.entity.UsersAuthorities;
import com.firerms.security.repository.UserRepository;
import com.firerms.security.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@SpringBootTest(classes=com.firerms.InspectionServiceApplication.class)
class CustomUserDetailsServiceUnitTests {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @MockBean
    UserRepository userRepository;

    @Test
    void loadUserByUsernameTest() {
        UsersAuthorities usersAuthorities = new UsersAuthorities(1L, "ADMIN", "ADMIN");
        User user = new User(1L, "username", "password12", "email@example.com",
                usersAuthorities, true, 1L);
        when(userRepository.findByUsernameAndFdid(user.getUsername(), user.getFdid())).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername() + "#" + user.getFdid());

        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    void loadUserByUsernameUserDisabledTest() {
        UsersAuthorities usersAuthorities = new UsersAuthorities(1L, "ADMIN", "ADMIN");
        User user = new User(1L, "username", "password12", "email@example.com",
                usersAuthorities, false, 1L);
        when(userRepository.findByUsernameAndFdid(user.getUsername(), user.getFdid())).thenReturn(user);

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(user.getUsername() + "#" + user.getFdid())
        );

        assertEquals("User not found with username: username", exception.getMessage());
    }

    @Test
    void loadUserByUsernameDoesNotExistTest() {
        String username = "username#1";

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(username)
        );

        assertEquals("User not found with username: username", exception.getMessage());
    }
}
