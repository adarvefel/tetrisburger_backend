package com.tetris.tetrisburger_backend.application.usecase.user;

import com.tetris.tetrisburger_backend.application.event.UserImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.CreateUserByAdminCommand;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de CreateUserByAdminUseCase")
class CreateUserByAdminUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CreateUserByAdminUseCase useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<UserImageUploadRequestedEvent> eventCaptor;

    private CreateUserByAdminCommand commandWithoutImage;
    private CreateUserByAdminCommand commandWithImage;
    private FileData validFileData;
    private User savedUser;

    @BeforeEach
    void setUp() {
        commandWithoutImage = new CreateUserByAdminCommand(
                "New User",
                "newuser@example.com",
                "password123",
                null,
                Role.CLIENT,
                "1234567890",
                1
        );

        // ✅ Usar lenient() para stubs opcionales que no se usan en todos los tests
        validFileData = mock(FileData.class);
        lenient().when(validFileData.isValid()).thenReturn(true);
        lenient().when(validFileData.bytes()).thenReturn(new byte[]{1, 2, 3});
        lenient().when(validFileData.contentType()).thenReturn("image/jpeg");
        lenient().when(validFileData.originalFilename()).thenReturn("profile.jpg");

        commandWithImage = new CreateUserByAdminCommand(
                "New User With Image",
                "userimage@example.com",
                "password123",
                validFileData,
                Role.CLIENT,
                "1234567890",
                1
        );

        savedUser = User.createByAdmin(
                "New User",
                "newuser@example.com",
                "$2a$10$hashedPassword",
                Role.CLIENT,
                "1234567890",
                null,
                null,
                1
        );
        savedUser.setIdUser(100);
    }

    @Nested
    @DisplayName("Creación exitosa sin imagen")
    class SuccessfulCreationWithoutImage {

        @Test
        @DisplayName("debería crear usuario sin imagen exitosamente")
        void shouldCreateUserWithoutImageSuccessfully() {
            // Given
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);

            // When
            User result = useCase.handle(commandWithoutImage);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);
            assertThat(result.getEmail()).isEqualTo("newuser@example.com");
            assertThat(result.getUserName()).isEqualTo("New User");
            assertThat(result.getRole()).isEqualTo(Role.CLIENT);
            assertThat(result.getPhone()).isEqualTo("1234567890");
            assertThat(result.getCreatedBy()).isEqualTo(1);

            verify(userRepository).existsByEmail("newuser@example.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).saveUser(userCaptor.capture());
            verify(eventPublisher, never()).publishEvent(any());

            User captured = userCaptor.getValue();
            assertThat(captured.getPassword()).isEqualTo("$2a$10$hashedPassword");
            assertThat(captured.getUserImageKey()).isNull();
            assertThat(captured.getUserImage()).isNull();
        }

        @Test
        @DisplayName("no debería publicar evento cuando no hay imagen")
        void shouldNotPublishEventWhenNoImage() {
            // Given
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);

            // When
            useCase.handle(commandWithoutImage);

            // Then
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Creación exitosa con imagen")
    class SuccessfulCreationWithImage {

        @Test
        @DisplayName("debería crear usuario y publicar evento de imagen")
        void shouldCreateUserAndPublishImageEvent() {
            // Given
            User savedUserWithImage = User.createByAdmin(
                    "New User With Image",
                    "userimage@example.com",
                    "$2a$10$hashedPassword",
                    Role.CLIENT,
                    "1234567890",
                    null,
                    null,
                    1
            );
            savedUserWithImage.setIdUser(200);

            when(userRepository.existsByEmail("userimage@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUserWithImage);

            // When
            User result = useCase.handle(commandWithImage);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(200);

            verify(userRepository).saveUser(any(User.class));
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            UserImageUploadRequestedEvent event = eventCaptor.getValue();
            assertThat(event.idUser()).isEqualTo(200);
            assertThat(event.fileBytes()).isEqualTo(new byte[]{1, 2, 3});
            assertThat(event.contentType()).isEqualTo("image/jpeg");
            assertThat(event.originalFileName()).isEqualTo("profile.jpg");
            assertThat(event.performedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("debería publicar evento con datos correctos de la imagen")
        void shouldPublishEventWithCorrectImageData() {
            // Given
            User savedUserWithImage = User.createByAdmin(
                    "New User With Image",
                    "userimage@example.com",
                    "$2a$10$hashedPassword",
                    Role.CLIENT,
                    "1234567890",
                    null,
                    null,
                    1
            );
            savedUserWithImage.setIdUser(200);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUserWithImage);

            // When
            useCase.handle(commandWithImage);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            UserImageUploadRequestedEvent event = eventCaptor.getValue();
            assertThat(event.idUser()).isEqualTo(200);
            assertThat(event.performedBy()).isEqualTo(1);
        }

        @Test
        @DisplayName("no debería publicar evento si la imagen no es válida")
        void shouldNotPublishEventWhenImageIsInvalid() {
            // Given
            FileData invalidFileData = mock(FileData.class);
            when(invalidFileData.isValid()).thenReturn(false);

            CreateUserByAdminCommand commandWithInvalidImage = new CreateUserByAdminCommand(
                    "New User",
                    "newuser@example.com",
                    "password123",
                    invalidFileData,
                    Role.CLIENT,
                    "1234567890",
                    1
            );

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);

            // When
            useCase.handle(commandWithInvalidImage);

            // Then
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Email duplicado")
    class DuplicateEmail {

        @Test
        @DisplayName("debería lanzar UserAlreadyExistsException cuando el email existe")
        void shouldThrowUserAlreadyExistsWhenEmailExists() {
            // Given
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> useCase.handle(commandWithoutImage))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("El email ya está registrado");

            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).saveUser(any());
            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    @Nested
    @DisplayName("Hash de contraseña")
    class PasswordHashing {

        @Test
        @DisplayName("debería hashear la contraseña antes de guardar")
        void shouldHashPasswordBeforeSaving() {
            // Given
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);

            // When
            useCase.handle(commandWithoutImage);

            // Then
            verify(passwordEncoder).encode("password123");
            verify(userRepository).saveUser(userCaptor.capture());
            User captured = userCaptor.getValue();
            assertThat(captured.getPassword()).isEqualTo("$2a$10$hashedPassword");
        }
    }

    @Nested
    @DisplayName("Creación con diferentes roles")
    class DifferentRoles {

        @Test
        @DisplayName("debería crear usuario con rol ADMIN")
        void shouldCreateUserWithAdminRole() {
            // Given
            CreateUserByAdminCommand adminCommand = new CreateUserByAdminCommand(
                    "Admin User",
                    "admin@example.com",
                    "password123",
                    null,
                    Role.ADMIN,
                    "1234567890",
                    1
            );

            User adminUser = User.createByAdmin(
                    "Admin User",
                    "admin@example.com",
                    "$2a$10$hashedPassword",
                    Role.ADMIN,
                    "1234567890",
                    null,
                    null,
                    1
            );
            adminUser.setIdUser(300);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(adminUser);

            // When
            User result = useCase.handle(adminCommand);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        }

        @Test
        @DisplayName("debería crear usuario con rol EMPLOYEE")
        void shouldCreateUserWithEmployeeRole() {
            // Given
            CreateUserByAdminCommand workerCommand = new CreateUserByAdminCommand(
                    "Worker User",
                    "worker@example.com",
                    "password123",
                    null,
                    Role.EMPLOYEE,
                    "1234567890",
                    1
            );

            User workerUser = User.createByAdmin(
                    "Worker User",
                    "worker@example.com",
                    "$2a$10$hashedPassword",
                    Role.EMPLOYEE,
                    "1234567890",
                    null,
                    null,
                    1
            );
            workerUser.setIdUser(400);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(workerUser);

            // When
            User result = useCase.handle(workerCommand);

            // Then
            assertThat(result.getRole()).isEqualTo(Role.EMPLOYEE);
        }
    }

    @Nested
    @DisplayName("Auditoría")
    class Audit {

        @Test
        @DisplayName("debería registrar createdBy del admin")
        void shouldRecordCreatedByFromAdmin() {
            // Given
            CreateUserByAdminCommand commandWithAdmin2 = new CreateUserByAdminCommand(
                    "New User",
                    "newuser@example.com",
                    "password123",
                    null,
                    Role.CLIENT,
                    "1234567890",
                    999
            );

            User userCreatedByAdmin2 = User.createByAdmin(
                    "New User",
                    "newuser@example.com",
                    "$2a$10$hashedPassword",
                    Role.CLIENT,
                    "1234567890",
                    null,
                    null,
                    999
            );
            userCreatedByAdmin2.setIdUser(500);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(userCreatedByAdmin2);

            // When
            User result = useCase.handle(commandWithAdmin2);

            // Then
            assertThat(result.getCreatedBy()).isEqualTo(999);
        }
    }

    @Nested
    @DisplayName("Flujo / Orden de llamadas")
    class FlowOrder {

        @Test
        @DisplayName("debería seguir el orden: exists -> encode -> save -> publishEvent")
        void shouldFollowCorrectCallOrder() {
            // Given
            User savedUserWithImage = User.createByAdmin(
                    "New User With Image",
                    "userimage@example.com",
                    "$2a$10$hashedPassword",
                    Role.CLIENT,
                    "1234567890",
                    null,
                    null,
                    1
            );
            savedUserWithImage.setIdUser(200);

            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUserWithImage);

            // When
            useCase.handle(commandWithImage);

            // Then
            var inOrder = inOrder(userRepository, passwordEncoder, eventPublisher);
            inOrder.verify(userRepository).existsByEmail("userimage@example.com");
            inOrder.verify(passwordEncoder).encode("password123");
            inOrder.verify(userRepository).saveUser(any(User.class));
            inOrder.verify(eventPublisher).publishEvent(any(UserImageUploadRequestedEvent.class));
        }
    }
}
