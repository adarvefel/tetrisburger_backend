package com.tetris.tetrisburger_backend.infrastructure.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Bucket por defecto: 30 requests por minuto
     * Uso: Endpoints generales sin autenticación específica
     */
    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createDefaultBucket());
    }

    private Bucket createDefaultBucket() {
        // 30 requests por minuto (1 cada 2 segundos)
        Bandwidth limit = Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Bucket para endpoints públicos: 5 requests por minuto
     * Uso: /api/auth/login, /api/auth/register
     * MUY RESTRICTIVO para prevenir ataques de fuerza bruta
     */
    public Bucket resolvePublicBucket(String key) {
        return cache.computeIfAbsent("public:" + key, k -> {
            // 5 requests por minuto (máximo 5 intentos de login)
            Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    /**
     * Bucket para usuarios autenticados: 60 requests por minuto
     * Uso: Endpoints protegidos con JWT válido
     * Permite 1 request por segundo
     */
    public Bucket resolveAuthenticatedBucket(String key) {
        return cache.computeIfAbsent("auth:" + key, k -> {
            // 60 requests por minuto (1 request por segundo)
            Bandwidth limit = Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1)));
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    /**
     * Bucket para endpoints CRÍTICOS: 3 requests cada 5 minutos
     * Uso: /reset-password, /forgot-password, /verify-otp
     * ULTRA RESTRICTIVO para operaciones sensibles
     */
    public Bucket resolveCriticalBucket(String key) {
        return cache.computeIfAbsent("critical:" + key, k -> {
            // 3 requests cada 5 minutos
            Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(5)));
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }
}
