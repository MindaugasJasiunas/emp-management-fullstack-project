package com.example.demo.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {
    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(user.getAuthorities()!=null && user.getAuthorities().size()>0){
//            return user.getAuthorities().stream()
//                    .map(Authority::getRoles)
//                    .flatMap(roles -> roles.stream())
//                    .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
//                    .collect(Collectors.toSet());

            return user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getRoleName())).collect(Collectors.toSet());
        }else{
            return new HashSet<>();
        }
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
        return true; // not implemented
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // not implemented
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}