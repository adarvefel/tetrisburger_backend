package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserAdminImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Role;
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
@DisplayName("Pruebas de UserAdminImageChangeListener")
class UserAdminImageChangeListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageStoragePort imageStoragePort;

    @InjectMocks
    private UserAdminImageChangeListener listener;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UserAdminImageChangeRequestedEvent event;
    private User user;
    private ImageUploadResult uploadResult;

    @BeforeEach
    void setUp() {
        byte[] bytes = new byte[]{9, 8, 7};
        event = new UserAdminImageChangeRequestedEvent(
                20,
                bytes,
                "image/png",
                "admin-new.png",
                "users/old-admin.png",
                999 // updatedBy (admin)
        );

        user = User.createByAdmin(
                "Target User",
                "target@example.com",
                "$2a$10$hashed",
                Role.CLIENT,
                null,
                "users/old-admin.png",
                "old-admin.png",
                1
        );
        user.setIdUser(20);

        uploadResult = new ImageUploadResult(
                "users/new-admin-key.png",
                "admin-new.png"
        );
    }

    @Nested
    @DisplayName("Flujo exitoso")
    class SuccessfulFlow {

        @Test
        @DisplayName("debería subir imagen, actualizar usuario con auditoría y borrar la anterior")
        void shouldUploadUpdateWithAuditAndDeleteOldImage() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(20)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.handle(event);

            // Then
            verify(imageStoragePort).uploadUserImage(
                    event.newFileBytes(),
                    event.contentType(),
                    event.originalFileName()
            );
            verify(userRepository).findUserById(20);
            verify(userRepository).saveUser(userCaptor.capture());
            verify(imageStoragePort).deleteImage("users/old-admin.png");

            User saved = userCaptor.getValue();
            assertThat(saved.getUserImageKey()).isEqualTo("users/new-admin-key.png");
            assertThat(saved.getUserImage()).isEqualTo("admin-new.png");
            assertThat(saved.getUpdatedBy()).isEqualTo(999);
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("no debería borrar imagen anterior si oldImageKey es null")
        void shouldNotDeleteOldImageWhenOldKeyIsNull() {
            // Given
            UserAdminImageChangeRequestedEvent noOldKeyEvent =
                    new UserAdminImageChangeRequestedEvent(
                            20,
                            event.newFileBytes(),
                            event.contentType(),
                            event.originalFileName(),
                            null,
                            event.updatedBy()
                    );

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(20)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.handle(noOldKeyEvent);

            // Then
            verify(imageStoragePort, never()).deleteImage(anyString());
        }

        @Test
        @DisplayName("no debería borrar imagen anterior si oldImageKey está en blanco")
        void shouldNotDeleteOldImageWhenOldKeyIsBlank() {
            // Given
            UserAdminImageChangeRequestedEvent blankOldKeyEvent =
                    new UserAdminImageChangeRequestedEvent(
                            20,
                            event.newFileBytes(),
                            event.contentType(),
                            event.originalFileName(),
                            "   ",
                            event.updatedBy()
                    );

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(20)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.handle(blankOldKeyEvent);

            // Then
            verify(imageStoragePort, never()).deleteImage(anyString());
        }
    }

    @Nested
    @DisplayName("Upload result nulo")
    class NullUploadResult {

        @Test
        @DisplayName("no debería persistir ni borrar nada si upload devuelve null")
        void shouldNotPersistOrDeleteWhenUploadReturnsNull() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(null);

            // When
            listener.handle(event);

            // Then
            verify(userRepository, never()).findUserById(anyInt());
            verify(userRepository, never()).saveUser(any());
            verify(imageStoragePort, never()).deleteImage(anyString());
        }
    }

    @Nested
    @DisplayName("Excepciones")
    class Exceptions {

        @Test
        @DisplayName("debería envolver excepciones de storage en ImageUploadException")
        void shouldWrapStorageExceptions() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenThrow(new RuntimeException("S3 error"));

            // When / Then
            assertThatThrownBy(() -> listener.handle(event))
                    .isInstanceOf(ImageUploadException.class)
                    .hasMessage("No se pudo subir la imagen del usuario")
                    .hasCauseInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("debería envolver UserNotFoundException desde persistNewImageKey")
        void shouldWrapUserNotFoundExceptionFromPersistNewImageKey() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(20)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> listener.handle(event))
                    .isInstanceOf(ImageUploadException.class)
                    .hasCauseInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("fallo al borrar imagen anterior no debe romper flujo")
        void deleteOldImageFailureShouldNotBreakFlow() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(20)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);
            doThrow(new RuntimeException("S3 delete failed"))
                    .when(imageStoragePort).deleteImage("users/old-admin.png");

            // When / Then
            assertThatCode(() -> listener.handle(event))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Método persistNewImageKey")
    class PersistNewImageKeyTests {

        @Test
        @DisplayName("debería actualizar imagen y auditoría correctamente")
        void shouldUpdateImageAndAuditCorrectly() {
            // Given
            when(userRepository.findUserById(20)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.persistNewImageKey(20, uploadResult, 999);

            // Then
            verify(userRepository).findUserById(20);
            verify(userRepository).saveUser(userCaptor.capture());

            User saved = userCaptor.getValue();
            assertThat(saved.getUserImageKey()).isEqualTo("users/new-admin-key.png");
            assertThat(saved.getUserImage()).isEqualTo("admin-new.png");
            assertThat(saved.getUpdatedBy()).isEqualTo(999);
        }

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando usuario no existe")
        void shouldThrowUserNotFoundWhenUserDoesNotExist() {
            // Given
            when(userRepository.findUserById(999)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> listener.persistNewImageKey(999, uploadResult, 1))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");
        }
    }
}
