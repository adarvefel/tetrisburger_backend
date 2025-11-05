package com.tetris.tetrisburger_backend.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Renombrado de variable: token (ya estaba bien)
        final String token = extractTokenFromRequest(request);

        final String username; // Renombrado de variable: username (antes 'nombreUsuario' o similar)

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }



        // Se usa 'extractUsername' que ya estaba en inglés y es correcto.
        username = jwtUtil.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Se usa 'validateToken' que ya estaba en inglés y es correcto.
            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }


    // ⭐️ Agrega este método para que el filtro se salte las rutas públicas ⭐️
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Ignora el filtro para cualquier ruta que empiece con /api/auth/
        return request.getServletPath().startsWith("/api/auth/");
    }

    /**
     * Renombrado de método: getTokenFromRequest -> extractTokenFromRequest
     * Propósito: Extrae el token JWT del header 'Authorization'.
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}