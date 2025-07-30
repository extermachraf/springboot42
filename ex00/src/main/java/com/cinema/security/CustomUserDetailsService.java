package com.cinema.security;

import com.cinema.entity.User;
import com.cinema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== CustomUserDetailsService called with username: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("=== User not found: " + username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        System.out.println("=== User found: " + user.getUsername() + ", Role: " + user.getRole() + ", Status: " + user.getStatus());
        System.out.println("=== Password hash: " + user.getPassword());

        UserPrincipal principal = new UserPrincipal(user);
        System.out.println("=== UserPrincipal created with authorities: " + principal.getAuthorities());
        System.out.println("=== UserPrincipal enabled: " + principal.isEnabled());

        return principal;
    }
}