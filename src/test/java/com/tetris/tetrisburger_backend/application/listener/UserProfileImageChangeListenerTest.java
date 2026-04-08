package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserProfileImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de UserProfileImageChangeListener")
class UserProfileImageChangeListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageStoragePort imageStoragePort;

    @InjectMocks
    private UserProfileImageChangeListener listener;

    private static final Integer USER_ID    = 1;
    private static final String CONTENT_TYPE = "image/jpeg";
    private static final String FILE_NAME    = "profile.jpg";
    private static final String OLD_KEY      = "old-key-123";
    private static final String NEW_KEY      = "new-key-456";
    private static final String IMAGE_URL    = "https://bucket.s3.amazonaws.com/new-key-456";
    private static final byte[] FILE_BYTES   = "fake-image-bytes".getBytes();

    // ── Helpers ───────────────────────────────────────────────────────────

    private UserProfileImageChangeRequestedEvent buildEvent(String oldKey) {
        UserProfileImageChangeRequestedEvent event = mock(UserProfileImageChangeRequestedEvent.class);
        lenient().when(event.fileBytes()).thenReturn(FILE_BYTES);
        lenient().when(event.contentType()).thenReturn(CONTENT_TYPE);
        lenient().when(event.originalFileName()).thenReturn(FILE_NAME);
        lenient().when(event.idUser()).thenReturn(USER_ID);
        lenient().when(event.oldImageKey()).thenReturn(oldKey);
        return event;
    }

    private ImageUploadResult setupSuccessfulUploadAndUser() {
        ImageUploadResult upload = mock(ImageUploadResult.class);
        when(upload.imageKey()).thenReturn(NEW_KEY);
        when(imageStoragePort.uploadUserImage(FILE_BYTES, CONTENT_TYPE, FILE_NAME)).thenReturn(upload);

        User user = mock(User.class);
        when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
        when(imageStoragePort.getImageUrl(NEW_KEY)).thenReturn(IMAGE_URL);
        return upload;
    }

    // ── handle ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("handle - flujo principal")
    class Handle {

        @Test
        @DisplayName("debería subir imagen, persistir y NO eliminar cuando no hay imagen anterior")
        void shouldUploadPersistAndNotDeleteWhenNoOldKey() {
            UserProfileImageChangeRequestedEvent event = buildEvent(null);
            ImageUploadResult upload = setupSuccessfulUploadAndUser();

            listener.handle(event);

            verify(imageStoragePort).uploadUserImage(FILE_BYTES, CONTENT_TYPE, FILE_NAME);
            verify(userRepository).findUserById(USER_ID);
            verify(imageStoragePort).getImageUrl(NEW_KEY);
            verify(userRepository).saveUser(any(User.class));
            verify(imageStoragePort, never()).deleteImage(any());
        }

        @Test
        @DisplayName("debería eliminar imagen anterior cuando oldImageKey tiene valor")
        void shouldDeleteOldImageWhenOldKeyIsPresent() {
            UserProfileImageChangeRequestedEvent event = buildEvent(OLD_KEY);
            setupSuccessfulUploadAndUser();

            listener.handle(event);

            verify(imageStoragePort).deleteImage(OLD_KEY);
        }

        @Test
        @DisplayName("NO debería eliminar imagen anterior cuando oldImageKey es null")
        void shouldNotDeleteWhenOldKeyIsNull() {
            UserProfileImageChangeRequestedEvent event = buildEvent(null);
            setupSuccessfulUploadAndUser();

            listener.handle(event);

            verify(imageStoragePort, never()).deleteImage(any());
        }

        @Test
        @DisplayName("NO debería eliminar imagen anterior cuando oldImageKey está en blanco")
        void shouldNotDeleteWhenOldKeyIsBlank() {
            UserProfileImageChangeRequestedEvent event = buildEvent("   ");
            setupSuccessfulUploadAndUser();

            listener.handle(event);

            verify(imageStoragePort, never()).deleteImage(any());
        }

        @Test
        @DisplayName("debería retornar sin persistir cuando uploadUserImage retorna null")
        void shouldReturnEarlyWhenUploadReturnsNull() {
            UserProfileImageChangeRequestedEvent event = buildEvent(null);
            when(imageStoragePort.uploadUserImage(FILE_BYTES, CONTENT_TYPE, FILE_NAME)).thenReturn(null);

            listener.handle(event);

            verify(userRepository, never()).findUserById(any());
            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería ignorar excepción de deleteImage (best-effort) y completar el flujo")
        void shouldIgnoreDeleteImageException() {
            UserProfileImageChangeRequestedEvent event = buildEvent(OLD_KEY);
            setupSuccessfulUploadAndUser();
            doThrow(new RuntimeException("S3 error")).when(imageStoragePort).deleteImage(OLD_KEY);

            // No debe propagar excepción y la imagen ya fue guardada
            assertDoesNotThrow(() -> listener.handle(event));
            verify(userRepository).saveUser(any(User.class));
        }

        @Test
        @DisplayName("debería ignorar silenciosamente cualquier excepción en el flujo principal")
        void shouldSwallowAnyExceptionInMainFlow() {
            UserProfileImageChangeRequestedEvent event = buildEvent(null);
            when(imageStoragePort.uploadUserImage(any(), any(), any()))
                    .thenThrow(new RuntimeException("Conexión fallida"));

            assertDoesNotThrow(() -> listener.handle(event));
        }
    }

    // ── persistUserImage ──────────────────────────────────────────────────

    @Nested
    @DisplayName("persistUserImage")
    class PersistUserImage {

        @Test
        @DisplayName("debería obtener URL, actualizar perfil y guardar usuario")
        void shouldGetUrlUpdateProfileAndSaveUser() {
            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(upload.imageKey()).thenReturn(NEW_KEY);

            User user = mock(User.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
            when(imageStoragePort.getImageUrl(NEW_KEY)).thenReturn(IMAGE_URL);

            listener.persistUserImage(USER_ID, upload);

            verify(imageStoragePort).getImageUrl(NEW_KEY);
            verify(user).updateImageProfile(NEW_KEY, IMAGE_URL);
            verify(userRepository).saveUser(user);
        }

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando el usuario no existe")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> listener.persistUserImage(USER_ID, upload));

            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería usar el imageKey del upload para obtener la URL")
        void shouldUseUploadImageKeyToGetUrl() {
            String specificKey = "specific-key-789";
            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(upload.imageKey()).thenReturn(specificKey);

            User user = mock(User.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
            when(imageStoragePort.getImageUrl(specificKey)).thenReturn(IMAGE_URL);

            listener.persistUserImage(USER_ID, upload);

            verify(imageStoragePort).getImageUrl(specificKey);
            verify(user).updateImageProfile(specificKey, IMAGE_URL);
        }
    }
}
