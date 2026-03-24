package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de UserImageUploadListener")
class UserImageUploadListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageStoragePort imageStoragePort;

    @InjectMocks
    private UserImageUploadListener listener;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UserImageUploadRequestedEvent validEvent;
    private User testUser;
    private ImageUploadResult uploadResult;

    // ── Constantes ────────────────────────────────────────────────────────
    private static final String IMAGE_KEY     = "users/profile-123.jpg";
    private static final String IMAGE_URL     = "https://bucket.s3.amazonaws.com/users/profile-123.jpg";
    private static final String NEW_IMAGE_KEY = "users/new-image.jpg";
    private static final String NEW_IMAGE_URL = "https://bucket.s3.amazonaws.com/users/new-image.jpg";

    @BeforeEach
    void setUp() {
        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};
        validEvent = new UserImageUploadRequestedEvent(100, imageBytes, "image/jpeg", "profile.jpg", 1);

        testUser = User.createClient("Test User", "test@example.com", "$2a$10$hashedPassword");
        testUser.setIdUser(100);

        uploadResult = new ImageUploadResult(IMAGE_KEY, "profile.jpg");

        // FIX: stub getImageUrl() por defecto — el listener lo llama para obtener la URL pública
        lenient().when(imageStoragePort.getImageUrl(IMAGE_KEY)).thenReturn(IMAGE_URL);
    }

    // ── Upload Exitoso ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Upload Exitoso")
    class SuccessfulUploadTests {

        @Test
        @DisplayName("debería subir imagen y actualizar usuario exitosamente")
        void shouldUploadImageAndUpdateUserSuccessfully() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(validEvent);

            verify(imageStoragePort).uploadUserImage(
                    validEvent.fileBytes(), "image/jpeg", "profile.jpg"
            );
            verify(userRepository).findUserById(100);
            verify(userRepository).saveUser(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserImageKey()).isEqualTo(IMAGE_KEY);
            assertThat(capturedUser.getUserImage()).isEqualTo(IMAGE_URL); // FIX: URL pública, no nombre de archivo
            assertThat(capturedUser.getUpdatedBy()).isEqualTo(1);
            assertThat(capturedUser.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería llamar al puerto de almacenamiento con datos correctos")
        void shouldCallStoragePortWithCorrectData() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(anyInt())).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(validEvent);

            verify(imageStoragePort).uploadUserImage(
                    validEvent.fileBytes(),
                    validEvent.contentType(),
                    validEvent.originalFileName()
            );
        }

        @Test
        @DisplayName("debería actualizar usuario con imageKey e imageUrl correctos")
        void shouldUpdateUserWithCorrectImageKeyAndUrl() {
            String customKey = "users/2026/02/16/custom-key.png";
            String customUrl = "https://bucket.s3.amazonaws.com/users/2026/02/16/custom-key.png";
            ImageUploadResult customResult = new ImageUploadResult(customKey, "custom-image.png");

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(customResult);
            when(imageStoragePort.getImageUrl(customKey)).thenReturn(customUrl); // FIX: stub para key específico
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(validEvent);

            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserImageKey()).isEqualTo(customKey);
            assertThat(capturedUser.getUserImage()).isEqualTo(customUrl); // FIX: URL pública
        }

        @Test
        @DisplayName("debería establecer updatedBy del evento")
        void shouldSetUpdatedByFromEvent() {
            UserImageUploadRequestedEvent eventWithAdmin = new UserImageUploadRequestedEvent(
                    100, new byte[]{1, 2, 3}, "image/png", "admin-upload.png", 999
            );

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(eventWithAdmin);

            verify(userRepository).saveUser(userCaptor.capture());
            assertThat(userCaptor.getValue().getUpdatedBy()).isEqualTo(999);
        }
    }

    // ── Upload Result Null ────────────────────────────────────────────────

    @Nested
    @DisplayName("Manejo de Upload Result Null")
    class NullUploadResultTests {

        @Test
        @DisplayName("no debería persistir cuando uploadResult es null")
        void shouldNotPersistWhenUploadResultIsNull() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(null);

            listener.onUserImageUploadRequested(validEvent);

            verify(imageStoragePort).uploadUserImage(
                    validEvent.fileBytes(), validEvent.contentType(), validEvent.originalFileName()
            );
            verify(userRepository, never()).findUserById(anyInt());
            verify(userRepository, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("debería retornar early cuando upload falla")
        void shouldReturnEarlyWhenUploadFails() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(null);

            listener.onUserImageUploadRequested(validEvent);

            verifyNoInteractions(userRepository);
        }
    }

    // ── Excepciones de Upload ─────────────────────────────────────────────

    @Nested
    @DisplayName("Manejo de Excepciones de Upload")
    class UploadExceptionHandlingTests {

        @Test
        @DisplayName("debería lanzar ImageUploadException cuando storage port falla")
        void shouldThrowImageUploadExceptionWhenStoragePortFails() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenThrow(new RuntimeException("S3 connection failed"));

            assertThatThrownBy(() -> listener.onUserImageUploadRequested(validEvent))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("No se pudo subir la imagen del usuario")
                    .hasCauseInstanceOf(RuntimeException.class)
                    .hasRootCauseMessage("S3 connection failed");

            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería propagar causa original en ImageUploadException")
        void shouldPropagateCauseInImageUploadException() {
            RuntimeException originalException = new RuntimeException("Network timeout");
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenThrow(originalException);

            assertThatThrownBy(() -> listener.onUserImageUploadRequested(validEvent))
                    .isInstanceOf(ImageUploadException.class)
                    .hasCause(originalException);
        }
    }

    // ── persistUserImage ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Método persistUserImage")
    class PersistUserImageTests {

        @Test
        @DisplayName("debería persistir imagen de usuario exitosamente")
        void shouldPersistUserImageSuccessfully() {
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.persistUserImage(100, uploadResult, 1);

            verify(userRepository).findUserById(100);
            verify(userRepository).saveUser(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserImageKey()).isEqualTo(IMAGE_KEY);
            assertThat(capturedUser.getUserImage()).isEqualTo(IMAGE_URL); // FIX: URL del setUp stub
            assertThat(capturedUser.getUpdatedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando usuario no existe")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            when(userRepository.findUserById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> listener.persistUserImage(999, uploadResult, 1))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");

            verify(userRepository).findUserById(999);
            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería actualizar imagen existente del usuario")
        void shouldUpdateExistingUserImage() {
            User userWithOldImage = User.createByAdmin(
                    "User", "user@example.com", "$2a$10$hashed",
                    Role.CLIENT, null, "users/old-image.jpg", "old.jpg", 1
            );
            userWithOldImage.setIdUser(100);

            ImageUploadResult newUpload = new ImageUploadResult(NEW_IMAGE_KEY, "new.jpg");
            when(imageStoragePort.getImageUrl(NEW_IMAGE_KEY)).thenReturn(NEW_IMAGE_URL); // FIX: stub para nuevo key
            when(userRepository.findUserById(100)).thenReturn(Optional.of(userWithOldImage));
            when(userRepository.saveUser(any(User.class))).thenReturn(userWithOldImage);

            listener.persistUserImage(100, newUpload, 1);

            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserImageKey()).isEqualTo(NEW_IMAGE_KEY);
            assertThat(capturedUser.getUserImage()).isEqualTo(NEW_IMAGE_URL); // FIX: URL pública nueva
        }
    }

    // ── Flujo Completo ────────────────────────────────────────────────────

    @Nested
    @DisplayName("Flujo Completo del Evento")
    class CompleteEventFlowTests {

        @Test
        @DisplayName("debería ejecutar el flujo completo en orden correcto")
        void shouldExecuteCompleteFlowInCorrectOrder() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(validEvent);

            var inOrder = inOrder(imageStoragePort, userRepository);
            inOrder.verify(imageStoragePort).uploadUserImage(
                    validEvent.fileBytes(), validEvent.contentType(), validEvent.originalFileName()
            );
            inOrder.verify(userRepository).findUserById(100);
            inOrder.verify(userRepository).saveUser(any(User.class));
        }

        @Test
        @DisplayName("debería manejar múltiples eventos secuencialmente")
        void shouldHandleMultipleEventsSequentially() {
            UserImageUploadRequestedEvent event1 = new UserImageUploadRequestedEvent(
                    100, new byte[]{1, 2}, "image/jpeg", "img1.jpg", 1
            );
            UserImageUploadRequestedEvent event2 = new UserImageUploadRequestedEvent(
                    101, new byte[]{3, 4}, "image/png", "img2.png", 1
            );

            User user1 = User.createClient("User1", "user1@example.com", "$2a$10$hashed");
            user1.setIdUser(100);
            User user2 = User.createClient("User2", "user2@example.com", "$2a$10$hashed");
            user2.setIdUser(101);

            ImageUploadResult result1 = new ImageUploadResult("key1", "img1.jpg");
            ImageUploadResult result2 = new ImageUploadResult("key2", "img2.png");

            when(imageStoragePort.uploadUserImage(eq(event1.fileBytes()), anyString(), anyString()))
                    .thenReturn(result1);
            when(imageStoragePort.uploadUserImage(eq(event2.fileBytes()), anyString(), anyString()))
                    .thenReturn(result2);
            lenient().when(imageStoragePort.getImageUrl("key1"))
                    .thenReturn("https://bucket.s3.amazonaws.com/key1");
            lenient().when(imageStoragePort.getImageUrl("key2"))
                    .thenReturn("https://bucket.s3.amazonaws.com/key2");
            when(userRepository.findUserById(100)).thenReturn(Optional.of(user1));
            when(userRepository.findUserById(101)).thenReturn(Optional.of(user2));
            when(userRepository.saveUser(any(User.class))).thenReturn(user1).thenReturn(user2);

            listener.onUserImageUploadRequested(event1);
            listener.onUserImageUploadRequested(event2);

            verify(imageStoragePort, times(2)).uploadUserImage(any(byte[].class), anyString(), anyString());
            verify(userRepository).findUserById(100);
            verify(userRepository).findUserById(101);
            verify(userRepository, times(2)).saveUser(any(User.class));
        }
    }

    // ── Validación de Datos del Evento ────────────────────────────────────

    @Nested
    @DisplayName("Validación de Datos del Evento")
    class EventDataValidationTests {

        @Test
        @DisplayName("debería manejar diferentes tipos de contenido")
        void shouldHandleDifferentContentTypes() {
            UserImageUploadRequestedEvent pngEvent = new UserImageUploadRequestedEvent(
                    100, new byte[]{1, 2, 3}, "image/png", "image.png", 1
            );

            when(imageStoragePort.uploadUserImage(any(byte[].class), eq("image/png"), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(pngEvent);

            // FIX: eq() en todos los args cuando se mezclan con matchers
            verify(imageStoragePort).uploadUserImage(
                    any(byte[].class),
                    eq("image/png"),
                    eq("image.png")
            );
        }

        @Test
        @DisplayName("debería manejar imágenes grandes")
        void shouldHandleLargeImages() {
            byte[] largeImageBytes = new byte[5 * 1024 * 1024];
            UserImageUploadRequestedEvent largeImageEvent = new UserImageUploadRequestedEvent(
                    100, largeImageBytes, "image/jpeg", "large-image.jpg", 1
            );

            when(imageStoragePort.uploadUserImage(eq(largeImageBytes), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(largeImageEvent);

            verify(imageStoragePort).uploadUserImage(
                    eq(largeImageBytes),
                    eq("image/jpeg"),
                    eq("large-image.jpg")
            );
        }

        @Test
        @DisplayName("debería manejar nombres de archivo con caracteres especiales")
        void shouldHandleFilenamesWithSpecialCharacters() {
            UserImageUploadRequestedEvent specialNameEvent = new UserImageUploadRequestedEvent(
                    100, new byte[]{1, 2, 3}, "image/jpeg", "foto perfíl ñ (2024).jpg", 1
            );

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), eq("foto perfíl ñ (2024).jpg")))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(specialNameEvent);

            // FIX: eq() en todos los args cuando se mezclan con matchers
            verify(imageStoragePort).uploadUserImage(
                    any(byte[].class),
                    eq("image/jpeg"),
                    eq("foto perfíl ñ (2024).jpg")
            );
        }
    }

    // ── Escenarios de Error ───────────────────────────────────────────────

    @Nested
    @DisplayName("Escenarios de Error")
    class ErrorScenariosTests {

        @Test
        @DisplayName("debería manejar excepción durante la búsqueda de usuario")
        void shouldHandleExceptionDuringUserLookup() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100))
                    .thenThrow(new RuntimeException("Database connection lost"));

            assertThatThrownBy(() -> listener.onUserImageUploadRequested(validEvent))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("No se pudo subir la imagen del usuario");

            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería manejar excepción durante el guardado")
        void shouldHandleExceptionDuringSave() {
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class)))
                    .thenThrow(new RuntimeException("Save failed"));

            assertThatThrownBy(() -> listener.onUserImageUploadRequested(validEvent))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("No se pudo subir la imagen del usuario");
        }
    }

    // ── Casos Edge ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Casos Edge")
    class EdgeCasesTests {

        @Test
        @DisplayName("debería manejar array de bytes vacío")
        void shouldHandleEmptyByteArray() {
            UserImageUploadRequestedEvent emptyEvent = new UserImageUploadRequestedEvent(
                    100, new byte[0], "image/jpeg", "empty.jpg", 1
            );

            when(imageStoragePort.uploadUserImage(eq(new byte[0]), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(100)).thenReturn(Optional.of(testUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(testUser);

            listener.onUserImageUploadRequested(emptyEvent);

            verify(imageStoragePort).uploadUserImage(
                    eq(new byte[0]),
                    eq("image/jpeg"),
                    eq("empty.jpg")
            );
        }

        @Test
        @DisplayName("debería manejar userId muy grande")
        void shouldHandleVeryLargeUserId() {
            UserImageUploadRequestedEvent largeIdEvent = new UserImageUploadRequestedEvent(
                    Integer.MAX_VALUE, new byte[]{1, 2, 3}, "image/jpeg", "test.jpg", 1
            );

            User userWithLargeId = User.createClient("User", "user@example.com", "$2a$10$hashed");
            userWithLargeId.setIdUser(Integer.MAX_VALUE);

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(Integer.MAX_VALUE)).thenReturn(Optional.of(userWithLargeId));
            when(userRepository.saveUser(any(User.class))).thenReturn(userWithLargeId);

            listener.onUserImageUploadRequested(largeIdEvent);

            verify(userRepository).findUserById(Integer.MAX_VALUE);
        }
    }
}
