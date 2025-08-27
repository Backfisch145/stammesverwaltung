package com.vcp.hessen.kurhessen.core.security;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserRepository;

import java.io.Serializable;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordEncoder passwordEncoder;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByUsername(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }

    public void changePassword(String oldPassword, String newPassword) {
        User user = get().get();
        if (!passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
            Notification.show("Your current password does not match!");
            return;
        }

        user.setHashedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logout();
    }

}
