package com.example.h3server.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class MyUserDetails implements UserDetails {

    private User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();

        user.getPermissions().forEach(p -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(p.getName()));
        });

        user.getRoles().forEach(r -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(r.getName()));
        });

        return grantedAuthorities;
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
        // TODO implement
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO implement
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO implement
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive();
    }
}
