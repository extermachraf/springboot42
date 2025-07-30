package com.cinema.security;

import com.cinema.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert Role enum to Spring Security authority
        // Spring Security expects "ROLE_" prefix
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // For now, accounts never expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // For now, accounts are never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // For now, credentials never expire
    }

    @Override
    public boolean isEnabled() {
        // Only CONFIRMED users are enabled
        return user.getStatus() == User.Status.CONFIRMED;
    }

    // Getter to access the underlying User entity
    public User getUser() {
        return user;
    }
}