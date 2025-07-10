package com.mikkkkkkka.gateway.model;

import com.mikkkkkkka.gateway.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()),
                new SimpleGrantedAuthority("OWNER_" + Optional
                        .ofNullable(user.getOwnerId())
                        .map(Object::toString)
                        .orElse("NONE")));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public Long getOwnerId() {
        return user.getOwnerId();
    }
}
