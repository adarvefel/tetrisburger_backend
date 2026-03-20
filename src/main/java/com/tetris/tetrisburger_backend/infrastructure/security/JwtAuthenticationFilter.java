package com.tetris.tetrisburger_backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
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
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String token = extractTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtil.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            writeErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token expirado",
                    "Tu sesión ha expirado. Por favor inicia sesión nuevamente.");
            return;

        } catch (MalformedJwtException e) {
            writeErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token malformado",
                    "El token no tiene un formato JWT válido.");
            return;

        } catch (SignatureException e) {
            writeErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Firma inválida",
                    "Token inválido.");
            return;

        } catch (UnsupportedJwtException e) {
            writeErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token no soportado",
                    "El tipo de token JWT proporcionado no está soportado.");
            return;

        } catch (IllegalArgumentException e) {
            writeErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Token inválido",
                    "El token está vacío o es nulo.");
            return;

        } catch (Exception e) {
            writeErrorResponse(response, request, HttpServletResponse.SC_UNAUTHORIZED,
                    "Error de autenticación",
                    "Ocurrió un error inesperado al procesar el token.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().startsWith("/api/auth/");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            HttpServletRequest request,
            int status,
            String error,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                status,
                error,
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
