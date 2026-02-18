package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateProfileUserCommand;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de UpdateProfileUserUseCase")
class UpdateProfileUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UpdateProfileUserUseCase useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User existingUser;
    private UpdateProfileUserCommand validCommandWithoutPassword;
    private UpdateProfileUserCommand validCommandWithPassword;

    @BeforeEach
    void setUp() {
        existingUser = User.createClient(
                "John Doe",
                "john@example.com",
                "$2a$10$oldHashedPassword"
        );
        existingUser.setIdUser(100);

        validCommandWithoutPassword = new UpdateProfileUserCommand(
                100,
                "John Updated",
                null, // Sin password
                "9876543210"
        );

        validCommandWithPassword = new UpdateProfileUserCommand(
                100,
                "John Updated",
                "newPassword123",
                "9876543210"
        );
    }

    @Nested
    @DisplayName("Actualización exitosa sin cambiar contraseña")
    class SuccessfulUpdateWithoutPassword {

        @Test
        @DisplayName("debería actualizar nombre y teléfono sin cambiar contraseña")
        void shouldUpdateNameAndPhoneWithoutChangingPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            String originalPassword = existingUser.getPassword();

            // When
            User result = useCase.handle(100, validCommandWithoutPassword);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);

            verify(userRepository).findUserById(100);
            verify(userRepository).saveUser(userCaptor.capture());
            verify(passwordEncoder, never()).encode(anyString());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserName()).isEqualTo("John Updated");
            assertThat(capturedUser.getPhone()).isEqualTo("9876543210");
            assertThat(capturedUser.getPassword()).isEqualTo(originalPassword);
            assertThat(capturedUser.getUpdatedBy()).isEqualTo(100);
            assertThat(capturedUser.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería mantener contraseña original cuando password es null")
        void shouldKeepOriginalPasswordWhenPasswordIsNull() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            String originalPassword = existingUser.getPassword();

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPassword()).isEqualTo(originalPassword);
        }

        @Test
        @DisplayName("debería actualizar solo nombre cuando phone es null")
        void shouldUpdateOnlyNameWhenPhoneIsNull() {
            // Given
            UpdateProfileUserCommand commandWithoutPhone = new UpdateProfileUserCommand(
                    100,
                    "New Name Only",
                    null,
                    null
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, commandWithoutPhone);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserName()).isEqualTo("New Name Only");
        }

        @Test
        @DisplayName("debería actualizar solo teléfono cuando userName es null")
        void shouldUpdateOnlyPhoneWhenUserNameIsNull() {
            // Given
            UpdateProfileUserCommand commandWithoutName = new UpdateProfileUserCommand(
                    100,
                    null,
                    null,
                    "5555555555"
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, commandWithoutName);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserName()).isEqualTo("John Doe"); // Sin cambios
            assertThat(capturedUser.getPhone()).isEqualTo("5555555555");
        }
    }

    @Nested
    @DisplayName("Actualización exitosa con cambio de contraseña")
    class SuccessfulUpdateWithPassword {

        @Test
        @DisplayName("debería actualizar perfil y hashear nueva contraseña")
        void shouldUpdateProfileAndHashNewPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            User result = useCase.handle(100, validCommandWithPassword);

            // Then
            assertThat(result).isNotNull();

            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).saveUser(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
            assertThat(capturedUser.getUserName()).isEqualTo("John Updated");
            assertThat(capturedUser.getPhone()).isEqualTo("9876543210");
        }

        @Test
        @DisplayName("debería ignorar password vacío sin cambiar el original")
        void shouldIgnoreBlankPasswordWithoutChangingOriginal() {
            // Given
            UpdateProfileUserCommand commandWithBlankPassword = new UpdateProfileUserCommand(
                    100,
                    "John Updated",
                    "   ", // Password en blanco
                    "9876543210"
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            String originalPassword = existingUser.getPassword();

            // When
            useCase.handle(100, commandWithBlankPassword);

            // Then
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository).saveUser(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPassword()).isEqualTo(originalPassword);
        }

        @Test
        @DisplayName("debería hashear password antes de guardar")
        void shouldHashPasswordBeforeSaving() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithPassword);

            // Then
            var inOrder = inOrder(passwordEncoder, userRepository);
            inOrder.verify(passwordEncoder).encode("newPassword123");
            inOrder.verify(userRepository).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Validación de permisos")
    class PermissionValidation {

        @Test
        @DisplayName("debería lanzar excepción cuando currentUserId no coincide con idUser")
        void shouldThrowExceptionWhenCurrentUserIdDoesNotMatchIdUser() {
            // Given
            Integer currentUserId = 100;
            UpdateProfileUserCommand command = new UpdateProfileUserCommand(
                    999, // Usuario diferente
                    "Hacker Name",
                    null,
                    "1111111111"
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(currentUserId, command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("No tienes permiso para modificar este perfil");

            verify(userRepository, never()).findUserById(anyInt());
            verify(userRepository, never()).saveUser(any());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("debería permitir actualización cuando currentUserId coincide")
        void shouldAllowUpdateWhenCurrentUserIdMatches() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            verify(userRepository).findUserById(100);
            verify(userRepository).saveUser(any());
        }

        @Test
        @DisplayName("debería usar currentUserId para auditoría")
        void shouldUseCurrentUserIdForAudit() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUpdatedBy()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("Usuario no encontrado")
    class UserNotFound {

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando usuario no existe")
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(100, validCommandWithoutPassword))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");

            verify(userRepository).findUserById(100);
            verify(userRepository, never()).saveUser(any());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    @DisplayName("Usuario no activo")
    class InactiveUser {

        @Test
        @DisplayName("debería lanzar excepción cuando usuario está eliminado")
        void shouldThrowExceptionWhenUserIsDeleted() {
            // Given
            existingUser.markAsDeleted(1);
            assertThat(existingUser.isActive()).isFalse();

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When / Then
            assertThatThrownBy(() -> useCase.handle(100, validCommandWithoutPassword))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("No puedes actualizar un usuario eliminado");

            verify(userRepository).findUserById(100);
            verify(userRepository, never()).saveUser(any());
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("debería validar estado activo antes de actualizar")
        void shouldValidateActiveStateBeforeUpdate() {
            // Given
            existingUser.markAsDeleted(1);

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When / Then
            assertThatThrownBy(() -> useCase.handle(100, validCommandWithPassword))
                    .isInstanceOf(IllegalStateException.class);

            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    @DisplayName("Auditoría")
    class Audit {

        @Test
        @DisplayName("debería registrar updatedBy del usuario actual")
        void shouldRecordUpdatedByFromCurrentUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUpdatedBy()).isEqualTo(100);
            assertThat(capturedUser.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería actualizar updatedAt al modificar perfil")
        void shouldUpdateUpdatedAtWhenModifyingProfile() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Orden de ejecución")
    class ExecutionOrder {

        @Test
        @DisplayName("debería seguir orden: validación permisos -> findUser -> validar activo -> updateProfile -> saveUser")
        void shouldFollowCorrectExecutionOrderWithoutPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            var inOrder = inOrder(userRepository);
            inOrder.verify(userRepository).findUserById(100);
            inOrder.verify(userRepository).saveUser(any(User.class));
            verify(passwordEncoder, never()).encode(anyString());
        }

        @Test
        @DisplayName("debería seguir orden: validación -> findUser -> validar activo -> updateProfile -> resetPassword -> saveUser")
        void shouldFollowCorrectExecutionOrderWithPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithPassword);

            // Then
            var inOrder = inOrder(userRepository, passwordEncoder);
            inOrder.verify(userRepository).findUserById(100);
            inOrder.verify(passwordEncoder).encode("newPassword123");
            inOrder.verify(userRepository).saveUser(any(User.class));
        }

        @Test
        @DisplayName("no debería guardar si validación de permisos falla")
        void shouldNotSaveIfPermissionValidationFails() {
            // Given
            UpdateProfileUserCommand command = new UpdateProfileUserCommand(
                    999,
                    "Name",
                    null,
                    "1111111111"
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(100, command))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(userRepository, never()).findUserById(anyInt());
            verify(userRepository, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Diferentes combinaciones de campos")
    class DifferentFieldCombinations {

        @Test
        @DisplayName("debería actualizar todos los campos cuando todos están presentes")
        void shouldUpdateAllFieldsWhenAllPresent() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserName()).isEqualTo("John Updated");
            assertThat(capturedUser.getPhone()).isEqualTo("9876543210");
            assertThat(capturedUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
        }

        @Test
        @DisplayName("debería actualizar solo campos no nulos")
        void shouldUpdateOnlyNonNullFields() {
            // Given
            UpdateProfileUserCommand partialCommand = new UpdateProfileUserCommand(
                    100,
                    "Only Name",
                    null,
                    null
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, partialCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserName()).isEqualTo("Only Name");
        }
    }

    @Nested
    @DisplayName("No publica eventos")
    class NoEventPublishing {

        @Test
        @DisplayName("no debería publicar eventos de imagen")
        void shouldNotPublishImageEvents() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("no debería publicar eventos incluso con password")
        void shouldNotPublishEventsEvenWithPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(100, validCommandWithPassword);

            // Then
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Captura de oldImageKey")
    class OldImageKeyCapture {

        @Test
        @DisplayName("debería capturar oldImageKey pero no usarlo")
        void shouldCaptureOldImageKeyButNotUseIt() {
            // Given
            User userWithImage = User.createByAdmin(
                    "John Doe",
                    "john@example.com",
                    "$2a$10$hash",
                    Role.CLIENT,
                    "1234567890",
                    "old-s3-key",
                    "old-image.jpg",
                    1
            );
            userWithImage.setIdUser(100);

            when(userRepository.findUserById(100)).thenReturn(Optional.of(userWithImage));
            when(userRepository.saveUser(any(User.class))).thenReturn(userWithImage);

            String expectedOldKey = userWithImage.getUserImageKey();

            // When
            useCase.handle(100, validCommandWithoutPassword);

            // Then
            // oldImageKey se captura en la línea del código pero no se usa
            assertThat(expectedOldKey).isEqualTo("old-s3-key");
            verify(eventPublisher, never()).publishEvent(any());
        }
    }
}
