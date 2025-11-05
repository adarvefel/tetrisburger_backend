package com.tetris.tetrisburger_backend.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * UserDetails personalizado que incluye el ID del usuario
 */
public class CustomUserDetails extends User {

    private final Integer id;

    public CustomUserDetails(
            Integer id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
