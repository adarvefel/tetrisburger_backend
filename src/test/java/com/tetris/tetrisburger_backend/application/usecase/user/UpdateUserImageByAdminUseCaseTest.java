package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.application.event.UserAdminImageChangeRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.UpdateUserImageByAdminCommand;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de UpdateUserImageByAdminUseCase")
class UpdateUserImageByAdminUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UpdateUserImageByAdminUseCase useCase;

    @Captor
    private ArgumentCaptor<UserAdminImageChangeRequestedEvent> eventCaptor;

    private User existingUser;
    private UpdateUserImageByAdminCommand validCommand;

    @BeforeEach
    void setUp() {
        existingUser = User.createByAdmin(
                "John Doe",
                "john@example.com",
                "$2a$10$hashedPassword",
                Role.CLIENT,
                "1234567890",
                "old-s3-key-123",
                "old-profile.jpg",
                1
        );
        existingUser.setIdUser(100);

        validCommand = new UpdateUserImageByAdminCommand(
                100,
                new byte[]{1, 2, 3},
                "image/jpeg",
                "new-admin-profile.jpg",
                99
        );
    }

    @Nested
    @DisplayName("Actualización exitosa")
    class SuccessfulUpdate {

        @Test
        @DisplayName("debería actualizar imagen por admin exitosamente")
        void shouldUpdateImageByAdminSuccessfully() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            User result = useCase.handle(validCommand);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);

            verify(userRepository).findUserById(100);
            verify(eventPublisher).publishEvent(any(UserAdminImageChangeRequestedEvent.class));
        }

        @Test
        @DisplayName("debería publicar evento con todos los datos correctos")
        void shouldPublishEventWithCorrectData() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();

            assertThat(event.idUser()).isEqualTo(100);
            assertThat(event.newFileBytes()).isEqualTo(new byte[]{1, 2, 3});
            assertThat(event.contentType()).isEqualTo("image/jpeg");
            assertThat(event.originalFileName()).isEqualTo("new-admin-profile.jpg");
            assertThat(event.oldImageKey()).isEqualTo("old-s3-key-123");
            assertThat(event.updatedBy()).isEqualTo(99);
        }

        @Test
        @DisplayName("debería capturar oldImageKey cuando usuario tiene imagen")
        void shouldCaptureOldImageKeyWhenUserHasImage() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();
            assertThat(event.oldImageKey()).isEqualTo("old-s3-key-123");
        }

        @Test
        @DisplayName("debería capturar oldImageKey como null cuando usuario no tiene imagen")
        void shouldCaptureOldImageKeyAsNullWhenUserHasNoImage() {
            // Given
            User userWithoutImage = User.createClient(
                    "Jane Doe",
                    "jane@example.com",
                    "$2a$10$hashedPassword"
            );
            userWithoutImage.setIdUser(200);

            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    200,
                    new byte[]{4, 5, 6},
                    "image/png",
                    "first-profile.jpg",
                    99
            );

            when(userRepository.findUserById(200)).thenReturn(Optional.of(userWithoutImage));

            // When
            useCase.handle(command);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();
            assertThat(event.oldImageKey()).isNull();
        }

        @Test
        @DisplayName("debería retornar el usuario sin modificar la imagen en BD")
        void shouldReturnUserWithoutModifyingImageInDb() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            String originalImageKey = existingUser.getUserImageKey();
            String originalImage = existingUser.getUserImage();

            // When
            User result = useCase.handle(validCommand);

            // Then
            // La imagen no debe cambiar en el objeto (solo se publica evento)
            assertThat(result.getUserImageKey()).isEqualTo(originalImageKey);
            assertThat(result.getUserImage()).isEqualTo(originalImage);
            verify(userRepository, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Validación de fileBytes")
    class FileBytesValidation {

        @Test
        @DisplayName("debería lanzar excepción cuando fileBytes es null")
        void shouldThrowExceptionWhenFileBytesIsNull() {
            // Given
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    null, // fileBytes null
                    "image/jpeg",
                    "profile.jpg",
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("fileBytes es requerido");

            verify(userRepository, never()).findUserById(anyInt());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("debería lanzar excepción cuando fileBytes está vacío")
        void shouldThrowExceptionWhenFileBytesIsEmpty() {
            // Given
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{}, // Array vacío
                    "image/jpeg",
                    "profile.jpg",
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("fileBytes es requerido");

            verify(userRepository, never()).findUserById(anyInt());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Validación de contentType")
    class ContentTypeValidation {

        @Test
        @DisplayName("debería lanzar excepción cuando contentType es null")
        void shouldThrowExceptionWhenContentTypeIsNull() {
            // Given
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{1, 2, 3},
                    null, // contentType null
                    "profile.jpg",
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("contentType es requerido");

            verify(userRepository, never()).findUserById(anyInt());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("debería lanzar excepción cuando contentType está vacío")
        void shouldThrowExceptionWhenContentTypeIsBlank() {
            // Given
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{1, 2, 3},
                    "   ", // contentType vacío
                    "profile.jpg",
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("contentType es requerido");

            verify(userRepository, never()).findUserById(anyInt());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("debería aceptar diferentes tipos de imagen")
        void shouldAcceptDifferentImageTypes() {
            // Given
            UpdateUserImageByAdminCommand commandPng = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{1, 2, 3},
                    "image/png",
                    "profile.png",
                    99
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(commandPng);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();
            assertThat(event.contentType()).isEqualTo("image/png");
        }
    }

    @Nested
    @DisplayName("Validación de originalFileName")
    class OriginalFileNameValidation {

        @Test
        @DisplayName("debería lanzar excepción cuando originalFileName es null")
        void shouldThrowExceptionWhenOriginalFileNameIsNull() {
            // Given
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    null, // originalFileName null
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("originalFileName es requerido");

            verify(userRepository, never()).findUserById(anyInt());
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("debería lanzar excepción cuando originalFileName está vacío")
        void shouldThrowExceptionWhenOriginalFileNameIsBlank() {
            // Given
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "   ", // originalFileName vacío
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("originalFileName es requerido");

            verify(userRepository, never()).findUserById(anyInt());
            verify(eventPublisher, never()).publishEvent(any());
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
                    .hasMessage("Usuario no encontrado");

            verify(userRepository).findUserById(100);
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("debería validar campos antes de buscar usuario")
        void shouldValidateFieldsBeforeFindingUser() {
            // Given
            UpdateUserImageByAdminCommand invalidCommand = new UpdateUserImageByAdminCommand(
                    100,
                    null, // fileBytes inválido
                    "image/jpeg",
                    "profile.jpg",
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(invalidCommand))
                    .isInstanceOf(IllegalArgumentException.class);

            // No debería buscar el usuario
            verify(userRepository, never()).findUserById(anyInt());
        }
    }

    @Nested
    @DisplayName("Diferentes roles de usuario")
    class DifferentUserRoles {

        @Test
        @DisplayName("debería actualizar imagen de usuario CLIENT")
        void shouldUpdateImageForClientUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            User result = useCase.handle(validCommand);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.CLIENT);
            verify(eventPublisher).publishEvent(any(UserAdminImageChangeRequestedEvent.class));
        }

        @Test
        @DisplayName("debería actualizar imagen de usuario ADMIN")
        void shouldUpdateImageForAdminUser() {
            // Given
            User adminUser = User.createByAdmin(
                    "Admin User",
                    "admin@example.com",
                    "$2a$10$hash",
                    Role.ADMIN,
                    "1234567890",
                    "admin-key",
                    "admin-image.jpg",
                    1
            );
            adminUser.setIdUser(200);

            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    200,
                    new byte[]{7, 8, 9},
                    "image/png",
                    "admin-new.png",
                    99
            );

            when(userRepository.findUserById(200)).thenReturn(Optional.of(adminUser));

            // When
            User result = useCase.handle(command);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.ADMIN);
            verify(eventPublisher).publishEvent(any(UserAdminImageChangeRequestedEvent.class));
        }

        @Test
        @DisplayName("debería actualizar imagen de usuario EMPLOYEE")
        void shouldUpdateImageForEmployeeUser() {
            // Given
            User employeeUser = User.createByAdmin(
                    "Employee User",
                    "employee@example.com",
                    "$2a$10$hash",
                    Role.EMPLOYEE,
                    "1234567890",
                    "employee-key",
                    "employee-image.jpg",
                    1
            );
            employeeUser.setIdUser(300);

            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    300,
                    new byte[]{10, 11, 12},
                    "image/jpeg",
                    "employee-new.jpg",
                    99
            );

            when(userRepository.findUserById(300)).thenReturn(Optional.of(employeeUser));

            // When
            User result = useCase.handle(command);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.EMPLOYEE);
            verify(eventPublisher).publishEvent(any(UserAdminImageChangeRequestedEvent.class));
        }
    }

    @Nested
    @DisplayName("Auditoría de updatedBy")
    class UpdatedByAudit {

        @Test
        @DisplayName("debería incluir updatedBy en el evento")
        void shouldIncludeUpdatedByInEvent() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();
            assertThat(event.updatedBy()).isEqualTo(99);
        }

        @Test
        @DisplayName("debería registrar diferente updatedBy correctamente")
        void shouldRecordDifferentUpdatedByCorrectly() {
            // Given
            UpdateUserImageByAdminCommand commandWithDifferentAdmin = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{1, 2, 3},
                    "image/jpeg",
                    "profile.jpg",
                    777 // Diferente admin
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(commandWithDifferentAdmin);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();
            assertThat(event.updatedBy()).isEqualTo(777);
        }
    }

    @Nested
    @DisplayName("Orden de ejecución")
    class ExecutionOrder {

        @Test
        @DisplayName("debería seguir el orden: validaciones -> findUser -> publishEvent")
        void shouldFollowCorrectExecutionOrder() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            var inOrder = inOrder(userRepository, eventPublisher);
            inOrder.verify(userRepository).findUserById(100);
            inOrder.verify(eventPublisher).publishEvent(any(UserAdminImageChangeRequestedEvent.class));
        }

        @Test
        @DisplayName("no debería buscar usuario si validaciones fallan")
        void shouldNotFindUserIfValidationsFail() {
            // Given
            UpdateUserImageByAdminCommand invalidCommand = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{}, // Vacío
                    "image/jpeg",
                    "profile.jpg",
                    99
            );

            // When / Then
            assertThatThrownBy(() -> useCase.handle(invalidCommand))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(userRepository, never()).findUserById(anyInt());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("No persistencia directa")
    class NoDirectPersistence {

        @Test
        @DisplayName("no debería llamar a saveUser del repositorio")
        void shouldNotCallSaveUser() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería delegar actualización de imagen al evento")
        void shouldDelegateImageUpdateToEvent() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            // Solo publica evento, no actualiza directamente
            verify(eventPublisher).publishEvent(any(UserAdminImageChangeRequestedEvent.class));
            verify(userRepository, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Diferencias con UpdateProfileImageUseCase")
    class DifferencesWithProfileImageUpdate {

        @Test
        @DisplayName("no debería validar que currentUserId coincida con idUser")
        void shouldNotValidateCurrentUserIdMatchesIdUser() {
            // Given
            // Admin 99 puede actualizar imagen de usuario 100 sin restricción
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            // No lanza excepción de permisos
            verify(eventPublisher).publishEvent(any(UserAdminImageChangeRequestedEvent.class));
        }

        @Test
        @DisplayName("debería publicar UserAdminImageChangeRequestedEvent con updatedBy")
        void shouldPublishAdminEventWithUpdatedBy() {
            // Given
            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(validCommand);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();

            // Evento específico para admin con updatedBy
            assertThat(event).isInstanceOf(UserAdminImageChangeRequestedEvent.class);
            assertThat(event.updatedBy()).isNotNull();
            assertThat(event.updatedBy()).isEqualTo(99);
        }
    }

    @Nested
    @DisplayName("Casos edge con tamaños de archivo")
    class FileSizeEdgeCases {

        @Test
        @DisplayName("debería aceptar archivo con 1 byte")
        void shouldAcceptFileWithOneByte() {
            // Given
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    new byte[]{1}, // Solo 1 byte
                    "image/jpeg",
                    "small.jpg",
                    99
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(command);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();
            assertThat(event.newFileBytes()).hasSize(1);
        }

        @Test
        @DisplayName("debería aceptar archivo grande")
        void shouldAcceptLargeFile() {
            // Given
            byte[] largeFile = new byte[1024 * 1024]; // 1 MB
            UpdateUserImageByAdminCommand command = new UpdateUserImageByAdminCommand(
                    100,
                    largeFile,
                    "image/jpeg",
                    "large.jpg",
                    99
            );

            when(userRepository.findUserById(100)).thenReturn(Optional.of(existingUser));

            // When
            useCase.handle(command);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserAdminImageChangeRequestedEvent event = eventCaptor.getValue();
            assertThat(event.newFileBytes()).hasSize(1024 * 1024);
        }
    }
}
