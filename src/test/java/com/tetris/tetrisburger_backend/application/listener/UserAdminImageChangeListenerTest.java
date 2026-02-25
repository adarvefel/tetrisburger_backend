package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserAdminImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
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
@DisplayName("Pruebas de UserAdminImageChangeListener")
class UserAdminImageChangeListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageStoragePort imageStoragePort;

    @InjectMocks
    private UserAdminImageChangeListener listener;

    private static final Integer USER_ID     = 1;
    private static final Integer UPDATED_BY  = 99;
    private static final String CONTENT_TYPE = "image/png";
    private static final String FILE_NAME    = "avatar.png";
    private static final String OLD_KEY      = "old-admin-key-123";
    private static final String NEW_KEY      = "new-admin-key-456";
    private static final String IMAGE_URL    = "https://bucket.s3.amazonaws.com/new-admin-key-456";
    private static final byte[] FILE_BYTES   = "fake-admin-bytes".getBytes();

    // ── Helpers ───────────────────────────────────────────────────────────

    private UserAdminImageChangeRequestedEvent buildEvent(String oldKey) {
        UserAdminImageChangeRequestedEvent event = mock(UserAdminImageChangeRequestedEvent.class);
        lenient().when(event.newFileBytes()).thenReturn(FILE_BYTES);
        lenient().when(event.contentType()).thenReturn(CONTENT_TYPE);
        lenient().when(event.originalFileName()).thenReturn(FILE_NAME);
        lenient().when(event.idUser()).thenReturn(USER_ID);
        lenient().when(event.oldImageKey()).thenReturn(oldKey);
        lenient().when(event.updatedBy()).thenReturn(UPDATED_BY);
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
            UserAdminImageChangeRequestedEvent event = buildEvent(null);
            setupSuccessfulUploadAndUser();

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
            UserAdminImageChangeRequestedEvent event = buildEvent(OLD_KEY);
            setupSuccessfulUploadAndUser();

            listener.handle(event);

            verify(imageStoragePort).deleteImage(OLD_KEY);
        }

        @Test
        @DisplayName("NO debería eliminar imagen anterior cuando oldImageKey es null")
        void shouldNotDeleteWhenOldKeyIsNull() {
            UserAdminImageChangeRequestedEvent event = buildEvent(null);
            setupSuccessfulUploadAndUser();

            listener.handle(event);

            verify(imageStoragePort, never()).deleteImage(any());
        }

        @Test
        @DisplayName("NO debería eliminar imagen anterior cuando oldImageKey está en blanco")
        void shouldNotDeleteWhenOldKeyIsBlank() {
            UserAdminImageChangeRequestedEvent event = buildEvent("  ");
            setupSuccessfulUploadAndUser();

            listener.handle(event);

            verify(imageStoragePort, never()).deleteImage(any());
        }

        @Test
        @DisplayName("debería retornar sin persistir cuando uploadUserImage retorna null")
        void shouldReturnEarlyWhenUploadReturnsNull() {
            UserAdminImageChangeRequestedEvent event = buildEvent(null);
            when(imageStoragePort.uploadUserImage(FILE_BYTES, CONTENT_TYPE, FILE_NAME)).thenReturn(null);

            listener.handle(event);

            verify(userRepository, never()).findUserById(any());
            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería lanzar ImageUploadException cuando uploadUserImage lanza excepción")
        void shouldThrowImageUploadExceptionWhenUploadFails() {
            UserAdminImageChangeRequestedEvent event = buildEvent(null);
            when(imageStoragePort.uploadUserImage(any(), any(), any()))
                    .thenThrow(new RuntimeException("Conexión S3 fallida"));

            assertThrows(ImageUploadException.class,
                    () -> listener.handle(event));
        }

        @Test
        @DisplayName("debería lanzar ImageUploadException cuando persistencia falla")
        void shouldThrowImageUploadExceptionWhenPersistFails() {
            UserAdminImageChangeRequestedEvent event = buildEvent(null);

            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(imageStoragePort.uploadUserImage(FILE_BYTES, CONTENT_TYPE, FILE_NAME)).thenReturn(upload);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty()); // user not found → UserNotFoundException → outer catch

            assertThrows(ImageUploadException.class,
                    () -> listener.handle(event));
        }

        @Test
        @DisplayName("debería ignorar excepción de deleteImage (best-effort) sin lanzar ImageUploadException")
        void shouldIgnoreDeleteImageExceptionAndNotThrow() {
            UserAdminImageChangeRequestedEvent event = buildEvent(OLD_KEY);
            setupSuccessfulUploadAndUser();
            doThrow(new RuntimeException("S3 delete error")).when(imageStoragePort).deleteImage(OLD_KEY);

            // El delete tiene try-catch interno — no debe propagarse al outer catch
            assertDoesNotThrow(() -> listener.handle(event));
            verify(userRepository).saveUser(any(User.class)); // la imagen fue guardada
        }

        @Test
        @DisplayName("debería incluir la excepción original como causa en ImageUploadException")
        void shouldWrapOriginalExceptionInImageUploadException() {
            UserAdminImageChangeRequestedEvent event = buildEvent(null);
            RuntimeException originalCause = new RuntimeException("Error original");
            when(imageStoragePort.uploadUserImage(any(), any(), any()))
                    .thenThrow(originalCause);

            ImageUploadException ex = assertThrows(ImageUploadException.class,
                    () -> listener.handle(event));
            assertEquals(originalCause, ex.getCause());
        }

        @Test
        @DisplayName("debería pasar updatedBy correcto a persistNewImageKey")
        void shouldPassCorrectUpdatedByToPersist() {
            UserAdminImageChangeRequestedEvent event = buildEvent(null);
            ImageUploadResult upload = setupSuccessfulUploadAndUser();

            User user = mock(User.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));

            listener.handle(event);

            verify(user).updateImage(NEW_KEY, IMAGE_URL, UPDATED_BY);
        }
    }

    // ── persistNewImageKey ────────────────────────────────────────────────

    @Nested
    @DisplayName("persistNewImageKey")
    class PersistNewImageKey {

        @Test
        @DisplayName("debería obtener URL, actualizar imagen y guardar usuario")
        void shouldGetUrlUpdateImageAndSaveUser() {
            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(upload.imageKey()).thenReturn(NEW_KEY);

            User user = mock(User.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
            when(imageStoragePort.getImageUrl(NEW_KEY)).thenReturn(IMAGE_URL);

            listener.persistNewImageKey(USER_ID, upload, UPDATED_BY);

            verify(imageStoragePort).getImageUrl(NEW_KEY);
            verify(user).updateImage(NEW_KEY, IMAGE_URL, UPDATED_BY);
            verify(userRepository).saveUser(user);
        }

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando el usuario no existe")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> listener.persistNewImageKey(USER_ID, upload, UPDATED_BY));

            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería pasar el updatedBy correcto al modelo")
        void shouldPassCorrectUpdatedByToModel() {
            Integer specificUpdatedBy = 55;
            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(upload.imageKey()).thenReturn(NEW_KEY);

            User user = mock(User.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
            when(imageStoragePort.getImageUrl(NEW_KEY)).thenReturn(IMAGE_URL);

            listener.persistNewImageKey(USER_ID, upload, specificUpdatedBy);

            verify(user).updateImage(NEW_KEY, IMAGE_URL, specificUpdatedBy);
        }

        @Test
        @DisplayName("debería usar el imageKey del upload para obtener la URL")
        void shouldUseUploadImageKeyToGetUrl() {
            String specificKey = "specific-admin-key-789";
            ImageUploadResult upload = mock(ImageUploadResult.class);
            when(upload.imageKey()).thenReturn(specificKey);

            User user = mock(User.class);
            when(userRepository.findUserById(USER_ID)).thenReturn(Optional.of(user));
            when(imageStoragePort.getImageUrl(specificKey)).thenReturn(IMAGE_URL);

            listener.persistNewImageKey(USER_ID, upload, UPDATED_BY);

            verify(imageStoragePort).getImageUrl(specificKey);
            verify(user).updateImage(specificKey, IMAGE_URL, UPDATED_BY);
        }
    }
}
