package com.tetris.tetrisburger_backend.infrastructure.rest.filter;

import com.tetris.tetrisburger_backend.infrastructure.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitConfig rateLimitConfig;

    public RateLimitFilter(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = getClientIP(request);
        String path = request.getRequestURI();

        Bucket bucket = selectBucket(path, clientIp, request);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.getWriter().write(
                    String.format(
                            "{\"error\":\"Demasiadas solicitudes\",\"message\":\"Límite excedido. Intenta nuevamente en %d segundos\"}",
                            waitForRefill
                    )
            );
        }
    }

    private Bucket selectBucket(String path, String clientIp, HttpServletRequest request) {
        if (path.startsWith("/api/auth/") || path.startsWith("/api/public/")) {
            return rateLimitConfig.resolvePublicBucket(clientIp);
        }

        if (request.getUserPrincipal() != null) {
            return rateLimitConfig.resolveAuthenticatedBucket(clientIp);
        }

        return rateLimitConfig.resolveBucket(clientIp);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
