package com.tetris.tetrisburger_backend.domain.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas de ImageUploadResult")
class ImageUploadResultTest {

    @Nested
    @DisplayName("Creación")
    class CreationTests {

        @Test
        @DisplayName("debería crear ImageUploadResult con valores válidos")
        void shouldCreateImageUploadResultWithValidValues() {
            // Given
            String imageKey = "users/profile-123.jpg";
            String originalFileName = "profile.jpg";

            // When
            ImageUploadResult result = new ImageUploadResult(imageKey, originalFileName);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.imageKey()).isEqualTo("users/profile-123.jpg");
            assertThat(result.originalFileName()).isEqualTo("profile.jpg");
        }

        @Test
        @DisplayName("debería aceptar valores nulos")
        void shouldAcceptNullValues() {
            // When
            ImageUploadResult result = new ImageUploadResult(null, null);

            // Then
            assertThat(result.imageKey()).isNull();
            assertThat(result.originalFileName()).isNull();
        }

        @Test
        @DisplayName("debería crear con imageKey de S3")
        void shouldCreateWithS3ImageKey() {
            // Given
            String s3Key = "tetrisburger/users/2026/02/16/user-123-profile.png";
            String fileName = "mi-foto-perfil.png";

            // When
            ImageUploadResult result = new ImageUploadResult(s3Key, fileName);

            // Then
            assertThat(result.imageKey()).isEqualTo(s3Key);
            assertThat(result.originalFileName()).isEqualTo(fileName);
        }
    }

    @Nested
    @DisplayName("Inmutabilidad")
    class ImmutabilityTests {

        @Test
        @DisplayName("debería ser inmutable (record)")
        void shouldBeImmutable() {
            // Given
            ImageUploadResult result = new ImageUploadResult("key123", "file.jpg");

            // Then - No existen setters
            assertThat(result.imageKey()).isEqualTo("key123");
            assertThat(result.originalFileName()).isEqualTo("file.jpg");
        }
    }

    @Nested
    @DisplayName("Igualdad y HashCode")
    class EqualityTests {

        @Test
        @DisplayName("dos ImageUploadResult con mismos valores deberían ser iguales")
        void twoImageUploadResultsWithSameValuesShouldBeEqual() {
            // Given
            ImageUploadResult result1 = new ImageUploadResult("key123", "file.jpg");
            ImageUploadResult result2 = new ImageUploadResult("key123", "file.jpg");

            // Then
            assertThat(result1).isEqualTo(result2);
            assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
        }

        @Test
        @DisplayName("dos ImageUploadResult con diferentes valores no deberían ser iguales")
        void twoImageUploadResultsWithDifferentValuesShouldNotBeEqual() {
            // Given
            ImageUploadResult result1 = new ImageUploadResult("key1", "file1.jpg");
            ImageUploadResult result2 = new ImageUploadResult("key2", "file2.jpg");

            // Then
            assertThat(result1).isNotEqualTo(result2);
        }
    }

    @Nested
    @DisplayName("Casos de Uso Reales")
    class RealWorldUseCasesTests {

        @Test
        @DisplayName("debería representar resultado de upload exitoso")
        void shouldRepresentSuccessfulUploadResult() {
            // Given - Simula respuesta de S3
            String s3Key = "users/2026-02-16-uuid-123-profile.jpg";
            String originalName = "mi-foto.jpg";

            // When
            ImageUploadResult result = new ImageUploadResult(s3Key, originalName);

            // Then
            assertThat(result.imageKey()).contains("users/");
            assertThat(result.imageKey()).contains("uuid-123");
            assertThat(result.originalFileName()).isEqualTo("mi-foto.jpg");
        }

        @Test
        @DisplayName("debería manejar nombres de archivo con espacios")
        void shouldHandleFileNamesWithSpaces() {
            // Given
            String key = "users/my-profile-pic.jpg";
            String fileName = "My Profile Pic.jpg";

            // When
            ImageUploadResult result = new ImageUploadResult(key, fileName);

            // Then
            assertThat(result.originalFileName()).isEqualTo("My Profile Pic.jpg");
        }
    }
}
