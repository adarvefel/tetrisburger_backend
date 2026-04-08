package com.tetris.tetrisburger_backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.infrastructure.config.RateLimitConfig;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.ErrorResponseDTO;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String ip = extractClientIp(request);
        String path = request.getServletPath();
        Bucket bucket = resolveBucketForPath(ip, path);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            writeRateLimitResponse(response, request);
        }
    }

    private Bucket resolveBucketForPath(String ip, String path) {
        if (path.contains("/forgot-password") || path.contains("/reset-password")) {
            return rateLimitConfig.resolveCriticalBucket(ip);
        }
        if (path.contains("/login") || path.contains("/register") || path.contains("/google")) {
            return rateLimitConfig.resolvePublicBucket(ip);
        }
        if (path.startsWith("/api/") && !path.startsWith("/api/auth/")) {
            return rateLimitConfig.resolveAuthenticatedBucket(ip);
        }
        return rateLimitConfig.resolveBucket(ip);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitResponse(
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                429,
                "Too Many Requests",
                "Demasiados intentos. Por favor espera un momento antes de continuar.",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}