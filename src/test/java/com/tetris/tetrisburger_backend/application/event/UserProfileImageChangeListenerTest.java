package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.UserProfileImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
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
@DisplayName("Pruebas de UserProfileImageChangeListener")
class UserProfileImageChangeListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageStoragePort imageStoragePort;

    @InjectMocks
    private UserProfileImageChangeListener listener;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private UserProfileImageChangeRequestedEvent event;
    private User user;
    private ImageUploadResult uploadResult;

    @BeforeEach
    void setUp() {
        byte[] bytes = new byte[]{1, 2, 3};
        event = new UserProfileImageChangeRequestedEvent(
                10,
                bytes,
                "image/jpeg",
                "new-profile.jpg",
                "users/old-profile.jpg"
        );

        user = User.createClient("Test User", "test@example.com", "$2a$10$hashed");
        user.setIdUser(10);

        uploadResult = new ImageUploadResult("users/new-profile-key.jpg", "new-profile.jpg");
    }

    @Nested
    @DisplayName("Flujo exitoso")
    class SuccessfulFlow {

        @Test
        @DisplayName("debería subir imagen, actualizar usuario y borrar la anterior")
        void shouldUploadUpdateAndDeleteOldImage() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(10)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.handle(event);

            // Then
            verify(imageStoragePort).uploadUserImage(
                    event.fileBytes(),
                    event.contentType(),
                    event.originalFileName()
            );
            verify(userRepository).findUserById(10);
            verify(userRepository).saveUser(userCaptor.capture());
            verify(imageStoragePort).deleteImage("users/old-profile.jpg");

            User saved = userCaptor.getValue();
            assertThat(saved.getUserImageKey()).isEqualTo("users/new-profile-key.jpg");
            assertThat(saved.getUserImage()).isEqualTo("new-profile.jpg");
        }

        @Test
        @DisplayName("no debería borrar imagen anterior si oldImageKey es null")
        void shouldNotDeleteOldImageWhenOldImageKeyIsNull() {
            // Given
            UserProfileImageChangeRequestedEvent noOldImageEvent =
                    new UserProfileImageChangeRequestedEvent(
                            10,
                            event.fileBytes(),
                            event.contentType(),
                            event.originalFileName(),
                            null
                    );

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(10)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.handle(noOldImageEvent);

            // Then
            verify(imageStoragePort, never()).deleteImage(anyString());
        }

        @Test
        @DisplayName("no debería borrar imagen anterior si oldImageKey está en blanco")
        void shouldNotDeleteOldImageWhenOldImageKeyIsBlank() {
            // Given
            UserProfileImageChangeRequestedEvent blankOldImageEvent =
                    new UserProfileImageChangeRequestedEvent(
                            10,
                            event.fileBytes(),
                            event.contentType(),
                            event.originalFileName(),
                            "   "
                    );

            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(10)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.handle(blankOldImageEvent);

            // Then
            verify(imageStoragePort, never()).deleteImage(anyString());
        }

        @Test
        @DisplayName("debería usar updateImageProfile (sin auditoría)")
        void shouldUseUpdateImageProfile() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(10)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);

            // When
            listener.handle(event);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User saved = userCaptor.getValue();
            assertThat(saved.getUserImageKey()).isEqualTo("users/new-profile-key.jpg");
            assertThat(saved.getUpdatedBy()).isNull(); // updateImageProfile no toca auditoría
        }
    }

    @Nested
    @DisplayName("Upload result nulo")
    class NullUploadResult {

        @Test
        @DisplayName("no debería tocar BD si upload devuelve null")
        void shouldNotPersistWhenUploadReturnsNull() {
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
                    .hasMessage("No se pudo subir la imagen del perfil")
                    .hasCauseInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("debería lanzar UserNotFoundException desde persistUserImage")
        void shouldThrowUserNotFoundFromPersistUserImage() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(10)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> listener.handle(event))
                    .isInstanceOf(ImageUploadException.class)
                    .hasCauseInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("el borrado de la imagen anterior nunca debe romper el flujo")
        void deleteOldImageShouldNotBreakFlow() {
            // Given
            when(imageStoragePort.uploadUserImage(any(byte[].class), anyString(), anyString()))
                    .thenReturn(uploadResult);
            when(userRepository.findUserById(10)).thenReturn(Optional.of(user));
            when(userRepository.saveUser(any(User.class))).thenReturn(user);
            doThrow(new RuntimeException("S3 delete failed"))
                    .when(imageStoragePort).deleteImage("users/old-profile.jpg");

            // When / Then
            assertThatCode(() -> listener.handle(event))
                    .doesNotThrowAnyException();
        }
    }
}
