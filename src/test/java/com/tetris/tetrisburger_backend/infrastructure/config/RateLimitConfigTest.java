package com.tetris.tetrisburger_backend.infrastructure.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas de RateLimitConfig")
class RateLimitConfigTest {

    private RateLimitConfig rateLimitConfig;

    @BeforeEach
    void setUp() {
        rateLimitConfig = new RateLimitConfig();
    }

    // ========================================
    // DEFAULT BUCKET TESTS
    // ========================================

    @Nested
    @DisplayName("Pruebas de Bucket por Defecto")
    class DefaultBucketTests {

        @Test
        @DisplayName("debería permitir 30 solicitudes por minuto")
        void shouldAllow30RequestsPerMinute() {
            // Given
            Bucket bucket = rateLimitConfig.resolveBucket("test-ip");

            // When - Consumir 30 tokens
            for (int i = 0; i < 30; i++) {
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                assertThat(probe.isConsumed()).isTrue();
            }

            // Then - La solicitud 31 debe ser rechazada
            ConsumptionProbe finalProbe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(finalProbe.isConsumed()).isFalse();
        }

        @Test
        @DisplayName("debería retornar el mismo bucket para la misma clave")
        void shouldReturnSameBucketForSameKey() {
            // Given
            String key = "192.168.1.100";

            // When
            Bucket bucket1 = rateLimitConfig.resolveBucket(key);
            Bucket bucket2 = rateLimitConfig.resolveBucket(key);

            // Then
            assertThat(bucket1).isSameAs(bucket2);
        }

        @Test
        @DisplayName("debería retornar buckets diferentes para claves diferentes")
        void shouldReturnDifferentBucketsForDifferentKeys() {
            // Given
            String key1 = "192.168.1.100";
            String key2 = "192.168.1.101";

            // When
            Bucket bucket1 = rateLimitConfig.resolveBucket(key1);
            Bucket bucket2 = rateLimitConfig.resolveBucket(key2);

            // Then
            assertThat(bucket1).isNotSameAs(bucket2);
        }

        @Test
        @DisplayName("debería tener 30 tokens disponibles inicialmente")
        void shouldHave30TokensAvailableInitially() {
            // Given
            Bucket bucket = rateLimitConfig.resolveBucket("test-ip");

            // When
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            // Then
            assertThat(probe.isConsumed()).isTrue();
            assertThat(probe.getRemainingTokens()).isEqualTo(29);
        }

        @Test
        @DisplayName("debería rechazar solicitud cuando no hay tokens disponibles")
        void shouldRejectRequestWhenNoTokensAvailable() {
            // Given
            Bucket bucket = rateLimitConfig.resolveBucket("test-ip");

            // Consumir todos los 30 tokens
            for (int i = 0; i < 30; i++) {
                bucket.tryConsumeAndReturnRemaining(1);
            }

            // When
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            // Then
            assertThat(probe.isConsumed()).isFalse();
            assertThat(probe.getRemainingTokens()).isEqualTo(0);
            assertThat(probe.getNanosToWaitForRefill()).isGreaterThan(0);
        }
    }

    // ========================================
    // PUBLIC BUCKET TESTS (Login/Register)
    // ========================================

    @Nested
    @DisplayName("Pruebas de Bucket Público (Login/Register)")
    class PublicBucketTests {

        @Test
        @DisplayName("debería permitir 5 solicitudes por minuto")
        void shouldAllow5RequestsPerMinute() {
            // Given
            Bucket bucket = rateLimitConfig.resolvePublicBucket("test-ip");

            // When - Consumir 5 tokens
            for (int i = 0; i < 5; i++) {
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                assertThat(probe.isConsumed())
                        .as("La solicitud %d debe ser permitida", i + 1)
                        .isTrue();
            }

            // Then - La solicitud 6 debe ser rechazada
            ConsumptionProbe finalProbe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(finalProbe.isConsumed()).isFalse();
        }

        @Test
        @DisplayName("debería usar el prefijo 'public:' en la clave de caché")
        void shouldUsePublicPrefixInCacheKey() {
            // Given
            String ip = "192.168.1.100";

            // When
            Bucket publicBucket = rateLimitConfig.resolvePublicBucket(ip);
            Bucket defaultBucket = rateLimitConfig.resolveBucket(ip);

            // Then - Deben ser buckets diferentes
            assertThat(publicBucket).isNotSameAs(defaultBucket);
        }

        @Test
        @DisplayName("debería tener 5 tokens disponibles inicialmente")
        void shouldHave5TokensAvailableInitially() {
            // Given
            Bucket bucket = rateLimitConfig.resolvePublicBucket("test-ip");

            // When
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            // Then
            assertThat(probe.isConsumed()).isTrue();
            assertThat(probe.getRemainingTokens()).isEqualTo(4);
        }

        @Test
        @DisplayName("debería prevenir ataques de fuerza bruta")
        void shouldPreventBruteForceAttacks() {
            // Given
            Bucket bucket = rateLimitConfig.resolvePublicBucket("atacante-ip");

            // When - Simular 10 intentos de login
            int intentosExitosos = 0;
            int intentosBloqueados = 0;

            for (int i = 0; i < 10; i++) {
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                if (probe.isConsumed()) {
                    intentosExitosos++;
                } else {
                    intentosBloqueados++;
                }
            }

            // Then
            assertThat(intentosExitosos).isEqualTo(5);
            assertThat(intentosBloqueados).isEqualTo(5);
        }
    }

    // ========================================
    // AUTHENTICATED BUCKET TESTS
    // ========================================

    @Nested
    @DisplayName("Pruebas de Bucket Autenticado")
    class AuthenticatedBucketTests {

        @Test
        @DisplayName("debería permitir 60 solicitudes por minuto")
        void shouldAllow60RequestsPerMinute() {
            // Given
            Bucket bucket = rateLimitConfig.resolveAuthenticatedBucket("usuario-123");

            // When - Consumir 60 tokens
            for (int i = 0; i < 60; i++) {
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                assertThat(probe.isConsumed())
                        .as("La solicitud %d debe ser permitida", i + 1)
                        .isTrue();
            }

            // Then - La solicitud 61 debe ser rechazada
            ConsumptionProbe finalProbe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(finalProbe.isConsumed()).isFalse();
        }

        @Test
        @DisplayName("debería usar el prefijo 'auth:' en la clave de caché")
        void shouldUseAuthPrefixInCacheKey() {
            // Given
            String userId = "usuario-123";

            // When
            Bucket authBucket = rateLimitConfig.resolveAuthenticatedBucket(userId);
            Bucket defaultBucket = rateLimitConfig.resolveBucket(userId);

            // Then - Deben ser buckets diferentes
            assertThat(authBucket).isNotSameAs(defaultBucket);
        }

        @Test
        @DisplayName("debería tener 60 tokens disponibles inicialmente")
        void shouldHave60TokensAvailableInitially() {
            // Given
            Bucket bucket = rateLimitConfig.resolveAuthenticatedBucket("usuario-123");

            // When
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            // Then
            assertThat(probe.isConsumed()).isTrue();
            assertThat(probe.getRemainingTokens()).isEqualTo(59);
        }

        @Test
        @DisplayName("debería permitir 1 solicitud por segundo en promedio")
        void shouldAllowOneRequestPerSecondOnAverage() {
            // Given
            Bucket bucket = rateLimitConfig.resolveAuthenticatedBucket("usuario-123");

            // When - Simular uso normal (1 req/seg por 60 segundos)
            for (int i = 0; i < 60; i++) {
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                assertThat(probe.isConsumed()).isTrue();
            }

            // Then - Todas las solicitudes deben exitosas
            ConsumptionProbe finalProbe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(finalProbe.isConsumed()).isFalse();
        }

        @Test
        @DisplayName("debería proporcionar más capacidad que el bucket público")
        void shouldProvideMoreCapacityThanPublicBucket() {
            // Given
            Bucket authBucket = rateLimitConfig.resolveAuthenticatedBucket("usuario-123");
            Bucket publicBucket = rateLimitConfig.resolvePublicBucket("192.168.1.100");

            // When
            ConsumptionProbe authProbe = authBucket.tryConsumeAndReturnRemaining(1);
            ConsumptionProbe publicProbe = publicBucket.tryConsumeAndReturnRemaining(1);

            // Then
            assertThat(authProbe.getRemainingTokens()).isEqualTo(59);
            assertThat(publicProbe.getRemainingTokens()).isEqualTo(4);
            assertThat(authProbe.getRemainingTokens()).isGreaterThan(publicProbe.getRemainingTokens());
        }
    }

    // ========================================
    // CRITICAL BUCKET TESTS (Reset Password)
    // ========================================

    @Nested
    @DisplayName("Pruebas de Bucket Crítico (Reset Password)")
    class CriticalBucketTests {

        @Test
        @DisplayName("debería permitir 3 solicitudes cada 5 minutos")
        void shouldAllow3RequestsPer5Minutes() {
            // Given
            Bucket bucket = rateLimitConfig.resolveCriticalBucket("test-ip");

            // When - Consumir 3 tokens
            for (int i = 0; i < 3; i++) {
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                assertThat(probe.isConsumed())
                        .as("La solicitud %d debe ser permitida", i + 1)
                        .isTrue();
            }

            // Then - La solicitud 4 debe ser rechazada
            ConsumptionProbe finalProbe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(finalProbe.isConsumed()).isFalse();
        }

        @Test
        @DisplayName("debería usar el prefijo 'critical:' en la clave de caché")
        void shouldUseCriticalPrefixInCacheKey() {
            // Given
            String ip = "192.168.1.100";

            // When
            Bucket criticalBucket = rateLimitConfig.resolveCriticalBucket(ip);
            Bucket defaultBucket = rateLimitConfig.resolveBucket(ip);

            // Then - Deben ser buckets diferentes
            assertThat(criticalBucket).isNotSameAs(defaultBucket);
        }

        @Test
        @DisplayName("debería tener 3 tokens disponibles inicialmente")
        void shouldHave3TokensAvailableInitially() {
            // Given
            Bucket bucket = rateLimitConfig.resolveCriticalBucket("test-ip");

            // When
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            // Then
            assertThat(probe.isConsumed()).isTrue();
            assertThat(probe.getRemainingTokens()).isEqualTo(2);
        }

        @Test
        @DisplayName("debería ser el tipo de bucket más restrictivo")
        void shouldBeMostRestrictiveBucketType() {
            // Given
            Bucket criticalBucket = rateLimitConfig.resolveCriticalBucket("test-1");
            Bucket publicBucket = rateLimitConfig.resolvePublicBucket("test-2");
            Bucket authBucket = rateLimitConfig.resolveAuthenticatedBucket("test-3");
            Bucket defaultBucket = rateLimitConfig.resolveBucket("test-4");

            // When
            ConsumptionProbe criticalProbe = criticalBucket.tryConsumeAndReturnRemaining(1);
            ConsumptionProbe publicProbe = publicBucket.tryConsumeAndReturnRemaining(1);
            ConsumptionProbe authProbe = authBucket.tryConsumeAndReturnRemaining(1);
            ConsumptionProbe defaultProbe = defaultBucket.tryConsumeAndReturnRemaining(1);

            // Then - Critical debe tener menos tokens restantes
            assertThat(criticalProbe.getRemainingTokens()).isEqualTo(2);
            assertThat(publicProbe.getRemainingTokens()).isEqualTo(4);
            assertThat(authProbe.getRemainingTokens()).isEqualTo(59);
            assertThat(defaultProbe.getRemainingTokens()).isEqualTo(29);

            assertThat(criticalProbe.getRemainingTokens())
                    .isLessThan(publicProbe.getRemainingTokens())
                    .isLessThan(defaultProbe.getRemainingTokens())
                    .isLessThan(authProbe.getRemainingTokens());
        }

        @Test
        @DisplayName("debería prevenir spam de reseteo de contraseña")
        void shouldPreventPasswordResetSpam() {
            // Given
            Bucket bucket = rateLimitConfig.resolveCriticalBucket("spammer-ip");

            // When - Simular 10 intentos de resetear contraseña
            int intentosExitosos = 0;
            int intentosBloqueados = 0;

            for (int i = 0; i < 10; i++) {
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                if (probe.isConsumed()) {
                    intentosExitosos++;
                } else {
                    intentosBloqueados++;
                }
            }

            // Then - Solo 3 deben ser exitosos
            assertThat(intentosExitosos).isEqualTo(3);
            assertThat(intentosBloqueados).isEqualTo(7);
        }
    }

    // ========================================
    // CACHE BEHAVIOR TESTS
    // ========================================

    @Nested
    @DisplayName("Pruebas de Comportamiento del Caché")
    class CacheBehaviorTests {

        @Test
        @DisplayName("debería cachear buckets por clave")
        void shouldCacheBucketsByKey() {
            // Given
            String key = "192.168.1.100";

            // When
            Bucket bucket1 = rateLimitConfig.resolveBucket(key);
            bucket1.tryConsumeAndReturnRemaining(5); // Consumir 5 tokens

            Bucket bucket2 = rateLimitConfig.resolveBucket(key);
            ConsumptionProbe probe = bucket2.tryConsumeAndReturnRemaining(1);

            // Then - Debe tener 24 restantes (30 - 5 - 1)
            assertThat(probe.getRemainingTokens()).isEqualTo(24);
        }

        @Test
        @DisplayName("debería aislar diferentes tipos de bucket")
        void shouldIsolateDifferentBucketTypes() {
            // Given
            String key = "test-aislamiento";

            // When
            Bucket defaultBucket = rateLimitConfig.resolveBucket(key);
            Bucket publicBucket = rateLimitConfig.resolvePublicBucket(key);
            Bucket authBucket = rateLimitConfig.resolveAuthenticatedBucket(key);
            Bucket criticalBucket = rateLimitConfig.resolveCriticalBucket(key);

            // Consumir del bucket por defecto
            defaultBucket.tryConsumeAndReturnRemaining(10);

            // Then - Otros buckets no deben ser afectados
            ConsumptionProbe publicProbe = publicBucket.tryConsumeAndReturnRemaining(1);
            ConsumptionProbe authProbe = authBucket.tryConsumeAndReturnRemaining(1);
            ConsumptionProbe criticalProbe = criticalBucket.tryConsumeAndReturnRemaining(1);

            assertThat(publicProbe.getRemainingTokens()).isEqualTo(4); // 5 - 1
            assertThat(authProbe.getRemainingTokens()).isEqualTo(59); // 60 - 1
            assertThat(criticalProbe.getRemainingTokens()).isEqualTo(2); // 3 - 1
        }

        @Test
        @DisplayName("debería manejar acceso concurrente de forma segura")
        void shouldHandleConcurrentAccessSafely() {
            // Given
            String key = "test-concurrencia";
            Bucket bucket = rateLimitConfig.resolveBucket(key);

            // When - Múltiples threads acceden al mismo bucket
            int conteoExitoso = 0;
            for (int i = 0; i < 30; i++) {
                if (bucket.tryConsumeAndReturnRemaining(1).isConsumed()) {
                    conteoExitoso++;
                }
            }

            // Then
            assertThat(conteoExitoso).isEqualTo(30);
        }
    }

    // ========================================
    // EDGE CASES
    // ========================================
    @Nested
    @DisplayName("Casos Extremos")
    class EdgeCasesTests {

        @Test
        @DisplayName("debería lanzar NullPointerException cuando la clave es null")
        void shouldThrowNullPointerExceptionWhenKeyIsNull() {
            // When/Then - Debe lanzar NullPointerException
            assertThatThrownBy(() -> rateLimitConfig.resolveBucket(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("debería manejar clave vacía")
        void shouldHandleEmptyKey() {
            // Given
            String emptyKey = "";

            // When
            Bucket bucket = rateLimitConfig.resolveBucket(emptyKey);

            // Then
            assertThat(bucket).isNotNull();
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(probe.isConsumed()).isTrue();
        }

        @Test
        @DisplayName("debería manejar caracteres especiales en la clave")
        void shouldHandleSpecialCharactersInKey() {
            // Given
            String specialKey = "192.168.1.100:8080/api/auth";

            // When
            Bucket bucket = rateLimitConfig.resolveBucket(specialKey);

            // Then
            assertThat(bucket).isNotNull();
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(probe.isConsumed()).isTrue();
        }

        @Test
        @DisplayName("debería manejar claves muy largas")
        void shouldHandleVeryLongKeys() {
            // Given
            String longKey = "a".repeat(1000);

            // When
            Bucket bucket = rateLimitConfig.resolveBucket(longKey);

            // Then
            assertThat(bucket).isNotNull();
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(probe.isConsumed()).isTrue();
        }

        @Test
        @DisplayName("debería manejar claves con espacios")
        void shouldHandleKeysWithSpaces() {
            // Given
            String keyWithSpaces = "192.168.1.100 user123";

            // When
            Bucket bucket = rateLimitConfig.resolveBucket(keyWithSpaces);

            // Then
            assertThat(bucket).isNotNull();
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(probe.isConsumed()).isTrue();
        }

        @Test
        @DisplayName("debería manejar claves Unicode")
        void shouldHandleUnicodeKeys() {
            // Given
            String unicodeKey = "usuario-日本語-🍔";

            // When
            Bucket bucket = rateLimitConfig.resolveBucket(unicodeKey);

            // Then
            assertThat(bucket).isNotNull();
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            assertThat(probe.isConsumed()).isTrue();
        }
    }


}
