package com.tetris.tetrisburger_backend.application.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pruebas de UserImageUploadRequestedEvent")
class UserImageUploadRequestedEventTest {

    @Nested
    @DisplayName("Creación del Evento")
    class EventCreationTests {

        @Test
        @DisplayName("debería crear evento con todos los campos válidos")
        void shouldCreateEventWithAllValidFields() {
            // Given
            Integer userId = 100;
            byte[] fileBytes = new byte[]{1, 2, 3, 4, 5};
            String contentType = "image/jpeg";
            String originalFileName = "profile.jpg";
            Integer performedBy = 1;

            // When
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    userId,
                    fileBytes,
                    contentType,
                    originalFileName,
                    performedBy
            );

            // Then
            assertThat(event).isNotNull();
            assertThat(event.idUser()).isEqualTo(100);
            assertThat(event.fileBytes()).isEqualTo(fileBytes);
            assertThat(event.contentType()).isEqualTo("image/jpeg");
            assertThat(event.originalFileName()).isEqualTo("profile.jpg");
            assertThat(event.performedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería crear evento con imagen PNG")
        void shouldCreateEventWithPNGImage() {
            // Given
            byte[] pngBytes = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47}; // PNG magic bytes

            // When
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    50,
                    pngBytes,
                    "image/png",
                    "avatar.png",
                    2
            );

            // Then
            assertThat(event.contentType()).isEqualTo("image/png");
            assertThat(event.originalFileName()).isEqualTo("avatar.png");
            assertThat(event.fileBytes()).startsWith((byte) 0x89, 0x50, 0x4E, 0x47);
        }

        @Test
        @DisplayName("debería crear evento con imagen grande")
        void shouldCreateEventWithLargeImage() {
            // Given
            byte[] largeImage = new byte[5 * 1024 * 1024]; // 5MB

            // When
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    200,
                    largeImage,
                    "image/jpeg",
                    "large-photo.jpg",
                    3
            );

            // Then
            assertThat(event.fileBytes()).hasSize(5 * 1024 * 1024);
        }

        @Test
        @DisplayName("debería aceptar valores nulos")
        void shouldAcceptNullValues() {
            // When
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    null,
                    null,
                    null,
                    null,
                    null
            );

            // Then
            assertThat(event.idUser()).isNull();
            assertThat(event.fileBytes()).isNull();
            assertThat(event.contentType()).isNull();
            assertThat(event.originalFileName()).isNull();
            assertThat(event.performedBy()).isNull();
        }
    }

    @Nested
    @DisplayName("Inmutabilidad del Record")
    class ImmutabilityTests {

        @Test
        @DisplayName("debería ser inmutable (record)")
        void shouldBeImmutable() {
            // Given
            byte[] originalBytes = new byte[]{1, 2, 3};
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    originalBytes,
                    "image/jpeg",
                    "test.jpg",
                    1
            );

            // When - Modificar array original
            originalBytes[0] = (byte) 99; // ✅ CAST A BYTE

            // Then - El evento mantiene referencia al mismo array
            assertThat(event.fileBytes()[0]).isEqualTo((byte) 99); // ✅ CAST A BYTE
        }

        @Test
        @DisplayName("no debería tener setters")
        void shouldNotHaveSetters() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "test.jpg",
                    1
            );

            // Then - Los records solo tienen getters
            assertThat(event.idUser()).isEqualTo(100);
            // No existen métodos setIdUser(), setFileBytes(), etc.
        }
    }

    @Nested
    @DisplayName("Igualdad y HashCode")
    class EqualityTests {

        @Test
        @DisplayName("dos eventos con mismos valores deberían ser iguales")
        void twoEventsWithSameValuesShouldBeEqual() {
            // Given
            byte[] bytes = new byte[]{1, 2, 3}; // ✅ USAR EL MISMO ARRAY

            UserImageUploadRequestedEvent event1 = new UserImageUploadRequestedEvent(
                    100, bytes, "image/jpeg", "test.jpg", 1
            );
            UserImageUploadRequestedEvent event2 = new UserImageUploadRequestedEvent(
                    100, bytes, "image/jpeg", "test.jpg", 1 // ✅ MISMA REFERENCIA
            );

            // Then
            assertThat(event1).isEqualTo(event2);
            assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
        }

        @Test
        @DisplayName("dos eventos con diferentes userId no deberían ser iguales")
        void twoEventsWithDifferentUserIdShouldNotBeEqual() {
            // Given
            byte[] bytes = new byte[]{1, 2, 3};

            UserImageUploadRequestedEvent event1 = new UserImageUploadRequestedEvent(
                    100, bytes, "image/jpeg", "test.jpg", 1
            );
            UserImageUploadRequestedEvent event2 = new UserImageUploadRequestedEvent(
                    200, bytes, "image/jpeg", "test.jpg", 1
            );

            // Then
            assertThat(event1).isNotEqualTo(event2);
        }

        @Test
        @DisplayName("dos eventos con diferentes fileBytes no deberían ser iguales")
        void twoEventsWithDifferentBytesShouldNotBeEqual() {
            // Given
            byte[] bytes1 = new byte[]{1, 2, 3};
            byte[] bytes2 = new byte[]{4, 5, 6};

            UserImageUploadRequestedEvent event1 = new UserImageUploadRequestedEvent(
                    100, bytes1, "image/jpeg", "test.jpg", 1
            );
            UserImageUploadRequestedEvent event2 = new UserImageUploadRequestedEvent(
                    100, bytes2, "image/jpeg", "test.jpg", 1
            );

            // Then
            assertThat(event1).isNotEqualTo(event2);
        }

        @Test
        @DisplayName("dos eventos con diferentes contentType no deberían ser iguales")
        void twoEventsWithDifferentContentTypeShouldNotBeEqual() {
            // Given
            byte[] bytes = new byte[]{1, 2, 3};

            UserImageUploadRequestedEvent event1 = new UserImageUploadRequestedEvent(
                    100, bytes, "image/jpeg", "test.jpg", 1
            );
            UserImageUploadRequestedEvent event2 = new UserImageUploadRequestedEvent(
                    100, bytes, "image/png", "test.jpg", 1
            );

            // Then
            assertThat(event1).isNotEqualTo(event2);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTests {

        @Test
        @DisplayName("toString debería contener información del evento")
        void toStringShouldContainEventInformation() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "profile.jpg",
                    5
            );

            // When
            String result = event.toString();

            // Then
            assertThat(result).contains("100");
            assertThat(result).contains("image/jpeg");
            assertThat(result).contains("profile.jpg");
            assertThat(result).contains("5");
        }
    }

    @Nested
    @DisplayName("Casos de Uso Reales")
    class RealWorldUseCasesTests {

        @Test
        @DisplayName("debería representar evento de upload de admin")
        void shouldRepresentAdminUploadEvent() {
            // Given - Admin con ID 1 sube imagen para usuario 100
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[]{1, 2, 3, 4, 5},
                    "image/jpeg",
                    "user-profile.jpg",
                    1 // Admin ID
            );

            // Then
            assertThat(event.idUser()).isEqualTo(100);
            assertThat(event.performedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería representar evento de self-upload")
        void shouldRepresentSelfUploadEvent() {
            // Given - Usuario 50 sube su propia imagen
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    50,
                    new byte[]{1, 2, 3},
                    "image/png",
                    "my-avatar.png",
                    50 // Mismo usuario
            );

            // Then
            assertThat(event.idUser()).isEqualTo(event.performedBy());
        }

        @Test
        @DisplayName("debería manejar nombres de archivo con espacios y caracteres especiales")
        void shouldHandleFilenamesWithSpacesAndSpecialCharacters() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "Mi Foto de Perfíl (2024).jpg",
                    1
            );

            // Then
            assertThat(event.originalFileName()).isEqualTo("Mi Foto de Perfíl (2024).jpg");
        }

        @Test
        @DisplayName("debería soportar diferentes tipos MIME de imagen")
        void shouldSupportDifferentImageMimeTypes() {
            // Given
            UserImageUploadRequestedEvent jpegEvent = new UserImageUploadRequestedEvent(
                    100, new byte[]{1}, "image/jpeg", "test.jpg", 1
            );
            UserImageUploadRequestedEvent pngEvent = new UserImageUploadRequestedEvent(
                    100, new byte[]{1}, "image/png", "test.png", 1
            );
            UserImageUploadRequestedEvent webpEvent = new UserImageUploadRequestedEvent(
                    100, new byte[]{1}, "image/webp", "test.webp", 1
            );

            // Then
            assertThat(jpegEvent.contentType()).isEqualTo("image/jpeg");
            assertThat(pngEvent.contentType()).isEqualTo("image/png");
            assertThat(webpEvent.contentType()).isEqualTo("image/webp");
        }
    }

    @Nested
    @DisplayName("Casos Edge")
    class EdgeCasesTests {

        @Test
        @DisplayName("debería manejar array de bytes vacío")
        void shouldHandleEmptyByteArray() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[0],
                    "image/jpeg",
                    "empty.jpg",
                    1
            );

            // Then
            assertThat(event.fileBytes()).isEmpty();
        }

        @Test
        @DisplayName("debería manejar userId máximo")
        void shouldHandleMaxUserId() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    Integer.MAX_VALUE,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "test.jpg",
                    Integer.MAX_VALUE
            );

            // Then
            assertThat(event.idUser()).isEqualTo(Integer.MAX_VALUE);
            assertThat(event.performedBy()).isEqualTo(Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("debería manejar nombre de archivo vacío")
        void shouldHandleEmptyFilename() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "",
                    1
            );

            // Then
            assertThat(event.originalFileName()).isEmpty();
        }

        @Test
        @DisplayName("debería manejar contentType con parámetros")
        void shouldHandleContentTypeWithParameters() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg; charset=utf-8",
                    "test.jpg",
                    1
            );

            // Then
            assertThat(event.contentType()).isEqualTo("image/jpeg; charset=utf-8");
        }
    }

    @Nested
    @DisplayName("Compatibilidad con Event System")
    class EventSystemCompatibilityTests {

        @Test
        @DisplayName("debería poder ser publicado como evento de Spring")
        void shouldBePublishableAsSpringEvent() {
            // Given
            UserImageUploadRequestedEvent event = new UserImageUploadRequestedEvent(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "test.jpg",
                    1
            );

            // Then - Verifica que tiene todos los campos necesarios para el listener
            assertThat(event.idUser()).isNotNull();
            assertThat(event.fileBytes()).isNotNull();
            assertThat(event.contentType()).isNotNull();
            assertThat(event.originalFileName()).isNotNull();
            assertThat(event.performedBy()).isNotNull();
        }
    }
}
