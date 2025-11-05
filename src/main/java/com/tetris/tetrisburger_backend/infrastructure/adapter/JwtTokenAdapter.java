// infrastructure/security/JwtTokenAdapter.java
package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.TokenPort;
import com.tetris.tetrisburger_backend.infrastructure.security.JwtUtil;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class JwtTokenAdapter implements TokenPort {

    private final JwtUtil jwtUtil;

    public JwtTokenAdapter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String generateToken(String email) {
        // JwtUtil necesita UserDetails, así que creamos uno simple
        UserDetails userDetails = new User(email, "", new ArrayList<>());
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public long getExpirationTime() {
        return jwtUtil.getExpirationTime();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            // Extraemos el email y validamos que no esté expirado
            String email = jwtUtil.extractUsername(token);
            UserDetails userDetails = new User(email, "", new ArrayList<>());
            return jwtUtil.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractEmail(String token) {
        return jwtUtil.extractUsername(token);
    }

    @Override
    public String generatePasswordResetToken(String email, long expirationTime) {
        return jwtUtil.createPasswordResetToken(email, expirationTime);
    }

    @Override
    public boolean validatePasswordResetToken(String token) {
        return jwtUtil.validatePasswordResetToken(token);
    }

    @Override
    public String extractEmailFromPasswordResetToken(String token) {
        return jwtUtil.extractEmailFromPasswordResetToken(token);
    }
}