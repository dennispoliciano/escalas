package com.github.dennispoliciano.escalas.auth;

import com.github.dennispoliciano.escalas.orgmembership.OrgMembership;
import com.github.dennispoliciano.escalas.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final User user;

    private final Collection<GrantedAuthority> authorities;


    public UserPrincipal(User user, List<OrgMembership> orgMemberships) {
        this.user = user;
        this.authorities = orgMemberships.stream()
                .map(membership -> new SimpleGrantedAuthority("ROLE_" + membership.getRole().name()))
                .collect(Collectors.toSet());
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getActive();
    }

}
