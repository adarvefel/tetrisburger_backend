package com.tetris.tetrisburger_backend.infrastructure.security;

import com.tetris.tetrisburger_backend.domain.enums.Role;
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
                        "Usuario no encontrado con el Email: " + email));

        Role role = domainUser.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));

        // Devuelve CustomUserDetails con ID de usuario
        return new CustomUserDetails(
                domainUser.getIdUser(),
                domainUser.getEmail(),
                domainUser.getPassword(),
                authorities
        );
    }
}
