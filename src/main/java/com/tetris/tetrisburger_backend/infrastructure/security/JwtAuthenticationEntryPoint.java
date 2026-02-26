package com.tetris.tetrisburger_backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String authHeader = request.getHeader("Authorization");

        String message;
        if (authHeader == null || authHeader.isBlank()) {
            message = "No se proporcionó token de autenticación.";
        } else if (!authHeader.startsWith("Bearer ")) {
            message = "Formato de token inválido.'.";
        } else {
            message = "No tienes permisos para acceder a este recurso.";
        }

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                401,
                "Unauthorized",
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
