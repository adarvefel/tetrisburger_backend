package com.tetris.tetrisburger_backend.infrastructure.security;
import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Generates the token based on user details
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Usamos el tiempo de expiración general para tokens de autenticación
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validates if the token is valid for the given user
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public Long getExpirationTime() {
        return expiration;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    //  MÉTODOS DE REESTABLECIMIENTO DE CONTRASEÑA (PASSWORD RESET) ⭐

    /**
     * Genera un token especializado para el proceso de reestablecimiento de contraseña.
     * * @param email El email del usuario (Subject)
     * @param expirationTime El tiempo específico de expiración para este token (debe ser corto)
     * @return El token JWT de reestablecimiento
     */
    public String createPasswordResetToken(String email, long expirationTime) { // <-- Renombrado
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "password_reset");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Usa el tiempo de expiración provisto para el reset
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida si el token es un token de reestablecimiento y si no ha expirado.
     * * @param token El token JWT a validar
     * @return true si es válido, false si no lo es o expiró
     */
    public boolean validatePasswordResetToken(String token) { // <-- Renombrado
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return "password_reset".equals(type) && !isTokenExpired(token);
        } catch (Exception e) {
            // Captura errores como tokens mal formados o con firma inválida
            return false;
        }
    }

    /**
     * Extrae el email del token de reestablecimiento de contraseña.
     * * @param token El token JWT
     * @return El email del usuario
     */
    public String extractEmailFromPasswordResetToken(String token) { // <-- Renombrado
        // Reutilizamos la lógica estándar de extracción de 'subject'
        return extractUsername(token);
    }

    public Integer getUserIdFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new InvalidTokenException("Usuario no autenticado");
        }

        // Obtener ID de CustomUserDetails
        if (auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getId();
        }

        throw new InvalidTokenException("No se puede extraer el ID");
    }
}