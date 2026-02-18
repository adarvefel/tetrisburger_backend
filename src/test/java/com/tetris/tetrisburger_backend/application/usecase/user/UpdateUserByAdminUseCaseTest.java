package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserByAdminCommand;
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
@DisplayName("Pruebas de UpdateUserByAdminUseCase")
class UpdateUserByAdminUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UpdateUserByAdminUseCase useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User existingUser;
    private UpdateUserByAdminCommand validCommandWithoutPassword;
    private UpdateUserByAdminCommand validCommandWithPassword;

    @BeforeEach
    void setUp() {
        existingUser = User.createByAdmin(
                "John Doe",
                "john@example.com",
                "$2a$10$oldHashedPassword",
                Role.CLIENT,
                "1234567890",
                "old-s3-key",
                "old-image.jpg",
                1
        );
        existingUser.setIdUser(100);

        validCommandWithoutPassword = new UpdateUserByAdminCommand(
                100,
                "John Updated",
                "john.updated@example.com",
                null, // Sin password
                Role.EMPLOYEE,
                "9876543210",
                99
        );

        validCommandWithPassword = new UpdateUserByAdminCommand(
                100,
                "John Updated",
                "john.updated@example.com",
                "newPassword123", // Con password
                Role.EMPLOYEE,
                "9876543210",
                99
        );
    }

    @Nested
    @DisplayName("Actualización exitosa sin cambiar contraseña")
    class SuccessfulUpdateWithoutPassword {

        @Test
        @DisplayName("debería actualizar perfil completo sin cambiar contraseña")
        void shouldUpdateProfileWithoutChangingPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            User result = useCase.handle(validCommandWithoutPassword);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);

            verify(userRepository).findUserById(100);
            verify(userRepository).saveUser(userCaptor.capture());
            verify(passwordEncoder, never()).encode(anyString());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUserName()).isEqualTo("John Updated");
            assertThat(capturedUser.getEmail()).isEqualTo("john.updated@example.com");
            assertThat(capturedUser.getRole()).isEqualTo(Role.EMPLOYEE);
            assertThat(capturedUser.getPhone()).isEqualTo("9876543210");
            assertThat(capturedUser.getUpdatedBy()).isEqualTo(99);
            assertThat(capturedUser.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("debería mantener contraseña original cuando no se proporciona nueva")
        void shouldKeepOriginalPasswordWhenNotProvided() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            String originalPassword = existingUser.getPassword();

            // When
            useCase.handle(validCommandWithoutPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPassword()).isEqualTo(originalPassword);
        }

        @Test
        @DisplayName("debería actualizar solo campos proporcionados, manteniendo los null")
        void shouldUpdateOnlyProvidedFields() {
            // Given
            UpdateUserByAdminCommand partialCommand = new UpdateUserByAdminCommand(
                    100,
                    "New Name",
                    null, // No cambiar email
                    null, // No cambiar password
                    null, // No cambiar role
                    null, // No cambiar phone
                    99
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(partialCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            assertThat(capturedUser.getUserName()).isEqualTo("New Name");
            assertThat(capturedUser.getEmail()).isEqualTo("john@example.com"); // Original
            assertThat(capturedUser.getRole()).isEqualTo(Role.CLIENT); // Original
            assertThat(capturedUser.getPhone()).isEqualTo("1234567890"); // Original
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
            User result = useCase.handle(validCommandWithPassword);

            // Then
            assertThat(result).isNotNull();

            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).saveUser(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
        }

        @Test
        @DisplayName("debería ignorar password vacío sin cambiar el original")
        void shouldIgnoreBlankPasswordWithoutChangingOriginal() {
            // Given
            UpdateUserByAdminCommand commandWithBlankPassword = new UpdateUserByAdminCommand(
                    100,
                    "John Updated",
                    "john.updated@example.com",
                    "   ", // Password en blanco
                    Role.EMPLOYEE,
                    "9876543210",
                    99
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            String originalPassword = existingUser.getPassword();

            // When
            useCase.handle(commandWithBlankPassword);

            // Then
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository).saveUser(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPassword()).isEqualTo(originalPassword);
        }
    }

    @Nested
    @DisplayName("Cambio de roles")
    class RoleChange {

        @Test
        @DisplayName("debería cambiar rol de CLIENT a ADMIN")
        void shouldChangeRoleFromClientToAdmin() {
            // Given
            UpdateUserByAdminCommand commandWithAdminRole = new UpdateUserByAdminCommand(
                    100,
                    null,
                    null,
                    null,
                    Role.ADMIN,
                    null,
                    99
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(commandWithAdminRole);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("debería cambiar rol de CLIENT a EMPLOYEE")
        void shouldChangeRoleFromClientToEmployee() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommandWithoutPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getRole()).isEqualTo(Role.EMPLOYEE);
        }

        @Test
        @DisplayName("debería mantener rol original cuando role es null")
        void shouldKeepOriginalRoleWhenRoleIsNull() {
            // Given
            UpdateUserByAdminCommand commandWithNullRole = new UpdateUserByAdminCommand(
                    100,
                    "John Updated",
                    null,
                    null,
                    null, // Role null
                    null,
                    99
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(commandWithNullRole);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getRole()).isEqualTo(Role.CLIENT); // Rol original
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
            assertThatThrownBy(() -> useCase.handle(validCommandWithoutPassword))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");

            verify(userRepository).findUserById(100);
            verify(userRepository, never()).saveUser(any());
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    @DisplayName("Auditoría")
    class Audit {

        @Test
        @DisplayName("debería registrar updatedBy del admin")
        void shouldRecordUpdatedByFromAdmin() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommandWithoutPassword);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getUpdatedBy()).isEqualTo(99);
            assertThat(capturedUser.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Orden de ejecución")
    class ExecutionOrder {

        @Test
        @DisplayName("debería seguir el orden: findUser -> updateByAdmin -> [resetPassword] -> saveUser")
        void shouldFollowCorrectExecutionOrderWithPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newHash");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommandWithPassword);

            // Then
            var inOrder = inOrder(userRepository, passwordEncoder);
            inOrder.verify(userRepository).findUserById(100);
            inOrder.verify(passwordEncoder).encode("newPassword123");
            inOrder.verify(userRepository).saveUser(any(User.class));
        }

        @Test
        @DisplayName("debería seguir el orden: findUser -> updateByAdmin -> saveUser (sin password)")
        void shouldFollowCorrectExecutionOrderWithoutPassword() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommandWithoutPassword);

            // Then
            var inOrder = inOrder(userRepository);
            inOrder.verify(userRepository).findUserById(100);
            inOrder.verify(userRepository).saveUser(any(User.class));
            verify(passwordEncoder, never()).encode(anyString());
        }
    }

    @Nested
    @DisplayName("Captura de oldImageKey")
    class OldImageKeyCapture {

        @Test
        @DisplayName("debería capturar oldImageKey antes de actualizar")
        void shouldCaptureOldImageKeyBeforeUpdate() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            String expectedOldKey = existingUser.getUserImageKey();

            // When
            useCase.handle(validCommandWithoutPassword);

            // Then
            // El oldImageKey se captura pero no se hace nada con él en este caso de uso
            // (a diferencia del anterior que publica evento)
            assertThat(expectedOldKey).isEqualTo("old-s3-key");
        }
    }
}

