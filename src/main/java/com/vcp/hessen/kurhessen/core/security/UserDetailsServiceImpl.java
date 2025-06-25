package com.vcp.hessen.kurhessen.core.security;

import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserRepository;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @SuppressWarnings("ConstantValue")
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username.toLowerCase());
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(),
                    getAuthorities(user));
        }
    }

    private static List<GrantedAuthority> getAuthorities(User user) {

        return user.getAuthorities();
    }

}
