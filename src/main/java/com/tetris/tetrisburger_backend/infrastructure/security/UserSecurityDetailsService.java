package com.tetris.tetrisburger_backend.infrastructure.security;

import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de seguridad para cargar usuarios desde el dominio.
 * Implementa UserDetailsService de Spring Security.
 * Pertenece a la capa de infrastructure.
**/

@Service
public class UserSecurityDetailsService implements UserDetailsService {

    private final UserRepository userPort;

    public UserSecurityDetailsService(UserRepository userPort) {
        this.userPort = userPort;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User domainUser = userPort.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        Role role = domainUser.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        // ✅ CAMBIO: Retorna CustomUserDetails con ID
        return new CustomUserDetails(
                domainUser.getIdUser(),  // ✅ AGREGAR ID
                domainUser.getEmail(),
                domainUser.getPassword(),
                authorities
        );
    }
}

