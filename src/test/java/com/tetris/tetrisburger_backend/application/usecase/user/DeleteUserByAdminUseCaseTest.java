package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.DeleteUserByAdminCommand;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de DeleteUserByAdminUseCase")
class DeleteUserByAdminUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DeleteUserByAdminUseCase useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User existingUser;
    private DeleteUserByAdminCommand validCommand;

    @BeforeEach
    void setUp() {
        existingUser = User.createByAdmin(
                "John Doe",
                "john@example.com",
                "$2a$10$hashedPassword",
                Role.CLIENT,
                "1234567890",
                "s3-key-123",
                "profile.jpg",
                1
        );
        existingUser.setIdUser(100);

        validCommand = new DeleteUserByAdminCommand(100, 99);
    }

    @Nested
    @DisplayName("Eliminación exitosa")
    class SuccessfulDeletion {

        @Test
        @DisplayName("debería marcar usuario como eliminado exitosamente")
        void shouldMarkUserAsDeletedSuccessfully() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).findUserById(100);
            verify(userRepository).saveUser(userCaptor.capture());

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isNotNull();
            assertThat(capturedUser.getDeletedBy()).isEqualTo(99);
            assertThat(capturedUser.isActive()).isFalse();
        }

        @Test
        @DisplayName("debería registrar deletedBy correctamente")
        void shouldRecordDeletedByCorrectly() {
            // Given
            DeleteUserByAdminCommand commandWithDifferentAdmin = new DeleteUserByAdminCommand(100, 777);

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(commandWithDifferentAdmin);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedBy()).isEqualTo(777);
        }

        @Test
        @DisplayName("debería establecer deletedAt con timestamp actual")
        void shouldSetDeletedAtWithCurrentTimestamp() {
            // Given
            LocalDateTime beforeDeletion = LocalDateTime.now();

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            assertThat(capturedUser.getDeletedAt()).isNotNull();
            assertThat(capturedUser.getDeletedAt()).isAfterOrEqualTo(beforeDeletion);
            assertThat(capturedUser.getDeletedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("debería cambiar estado de activo a inactivo")
        void shouldChangeStateFromActiveToInactive() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            assertThat(existingUser.isActive()).isTrue(); // Antes de eliminar

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.isActive()).isFalse(); // Después de eliminar
        }

        @Test
        @DisplayName("debería mantener los demás datos del usuario intactos")
        void shouldKeepOtherUserDataIntact() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            // Datos originales deben permanecer sin cambios
            assertThat(capturedUser.getIdUser()).isEqualTo(100);
            assertThat(capturedUser.getUserName()).isEqualTo("John Doe");
            assertThat(capturedUser.getEmail()).isEqualTo("john@example.com");
            assertThat(capturedUser.getRole()).isEqualTo(Role.CLIENT);
            assertThat(capturedUser.getPhone()).isEqualTo("1234567890");
            assertThat(capturedUser.getUserImageKey()).isEqualTo("s3-key-123");
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
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado con ID: 100");

            verify(userRepository).findUserById(100);
            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería incluir el ID del usuario en el mensaje de error")
        void shouldIncludeUserIdInErrorMessage() {
            // Given
            DeleteUserByAdminCommand commandWithDifferentId = new DeleteUserByAdminCommand(999, 99);
            when(userRepository.findUserById(999)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(commandWithDifferentId))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("Diferentes roles de usuario")
    class DifferentUserRoles {

        @Test
        @DisplayName("debería eliminar usuario con rol CLIENT")
        void shouldDeleteClientUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isNotNull();
            assertThat(capturedUser.getRole()).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("debería eliminar usuario con rol ADMIN")
        void shouldDeleteAdminUser() {
            // Given
            User adminUser = User.createByAdmin(
                    "Admin User",
                    "admin@example.com",
                    "$2a$10$hash",
                    Role.ADMIN,
                    "1234567890",
                    null,
                    null,
                    1
            );
            adminUser.setIdUser(200);

            DeleteUserByAdminCommand deleteAdminCommand = new DeleteUserByAdminCommand(200, 99);

            when(userRepository.findUserById(200)).thenReturn(Optional.of(adminUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(adminUser);

            // When
            useCase.handle(deleteAdminCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isNotNull();
            assertThat(capturedUser.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("debería eliminar usuario con rol EMPLOYEE")
        void shouldDeleteEmployeeUser() {
            // Given
            User employeeUser = User.createByAdmin(
                    "Employee User",
                    "employee@example.com",
                    "$2a$10$hash",
                    Role.EMPLOYEE,
                    "1234567890",
                    null,
                    null,
                    1
            );
            employeeUser.setIdUser(300);

            DeleteUserByAdminCommand deleteEmployeeCommand = new DeleteUserByAdminCommand(300, 99);

            when(userRepository.findUserById(300)).thenReturn(Optional.of(employeeUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(employeeUser);

            // When
            useCase.handle(deleteEmployeeCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isNotNull();
            assertThat(capturedUser.getRole()).isEqualTo(Role.EMPLOYEE);
        }
    }

    @Nested
    @DisplayName("Usuario con y sin imagen")
    class UserWithAndWithoutImage {

        @Test
        @DisplayName("debería eliminar usuario con imagen")
        void shouldDeleteUserWithImage() {
            // Given - existingUser ya tiene imagen
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            assertThat(existingUser.getUserImageKey()).isNotNull();

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isNotNull();
            // La imagen debe seguir ahí después del soft delete
            assertThat(capturedUser.getUserImageKey()).isEqualTo("s3-key-123");
        }

        @Test
        @DisplayName("debería eliminar usuario sin imagen")
        void shouldDeleteUserWithoutImage() {
            // Given
            User userWithoutImage = User.createClient(
                    "Jane Doe",
                    "jane@example.com",
                    "$2a$10$hash"
            );
            userWithoutImage.setIdUser(400);

            DeleteUserByAdminCommand deleteCommand = new DeleteUserByAdminCommand(400, 99);

            when(userRepository.findUserById(400)).thenReturn(Optional.of(userWithoutImage));
            when(userRepository.saveUser(any(User.class))).thenReturn(userWithoutImage);

            // When
            useCase.handle(deleteCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isNotNull();
            assertThat(capturedUser.getUserImageKey()).isNull();
        }
    }

    @Nested
    @DisplayName("Orden de ejecución")
    class ExecutionOrder {

        @Test
        @DisplayName("debería seguir el orden: findUserById -> markAsDeleted -> saveUser")
        void shouldFollowCorrectExecutionOrder() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            var inOrder = inOrder(userRepository);
            inOrder.verify(userRepository).findUserById(100);
            inOrder.verify(userRepository).saveUser(any(User.class));
        }

        @Test
        @DisplayName("no debería guardar cuando usuario no existe")
        void shouldNotSaveWhenUserDoesNotExist() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(UserNotFoundException.class);

            verify(userRepository).findUserById(100);
            verify(userRepository, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Verificación de llamadas al repositorio")
    class RepositoryCallVerification {

        @Test
        @DisplayName("debería llamar a findUserById exactamente una vez")
        void shouldCallFindUserByIdExactlyOnce() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository, times(1)).findUserById(100);
        }

        @Test
        @DisplayName("debería llamar a saveUser exactamente una vez")
        void shouldCallSaveUserExactlyOnce() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository, times(1)).saveUser(any(User.class));
        }

        @Test
        @DisplayName("debería pasar el usuario correcto a saveUser")
        void shouldPassCorrectUserToSaveUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser).isSameAs(existingUser);
        }
    }

    @Nested
    @DisplayName("Auditoría y rastreabilidad")
    class AuditAndTraceability {

        @Test
        @DisplayName("debería registrar quién eliminó el usuario")
        void shouldRecordWhoDeletedTheUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedBy()).isEqualTo(99);
        }

        @Test
        @DisplayName("debería registrar cuándo se eliminó el usuario")
        void shouldRecordWhenUserWasDeleted() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isNotNull();
            assertThat(capturedUser.getDeletedAt()).isInstanceOf(LocalDateTime.class);
        }

        @Test
        @DisplayName("deletedAt debería ser posterior a createdAt")
        void deletedAtShouldBeAfterCreatedAt() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            LocalDateTime createdAt = existingUser.getCreatedAt();

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getDeletedAt()).isAfter(createdAt);
        }
    }

    @Nested
    @DisplayName("Eliminación idempotente")
    class IdempotentDeletion {

        @Test
        @DisplayName("debería permitir eliminar usuario ya eliminado")
        void shouldAllowDeletingAlreadyDeletedUser() {
            // Given
            existingUser.markAsDeleted(50); // Ya eliminado previamente
            assertThat(existingUser.isActive()).isFalse();

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            LocalDateTime firstDeletedAt = existingUser.getDeletedAt();

            // When
            useCase.handle(validCommand); // Eliminar de nuevo

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User capturedUser = userCaptor.getValue();

            // Debería actualizar deletedAt y deletedBy
            assertThat(capturedUser.getDeletedAt()).isAfterOrEqualTo(firstDeletedAt);
            assertThat(capturedUser.getDeletedBy()).isEqualTo(99); // Nuevo deletedBy
            assertThat(capturedUser.isActive()).isFalse();
        }
    }
}

