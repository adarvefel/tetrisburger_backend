package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.exception.InvalidRecaptchaException;
import com.tetris.tetrisburger_backend.domain.exception.UserAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.enums.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.user.command.RegisterUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de RegisterUserUseCase")
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailPort emailPort;

    @Mock
    private RecaptchaPort recaptchaPort;

    @InjectMocks
    private RegisterUserUseCase useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private RegisterUserCommand validCommand;
    private User savedUser;

    @BeforeEach
    void setUp() {
        validCommand = new RegisterUserCommand(
                "Nuevo Usuario",
                "newuser@example.com",
                "password123",
                "recaptcha-token"
        );

        savedUser = User.createClient(
                "Nuevo Usuario",
                "newuser@example.com",
                "$2a$10$hashedPassword"
        );
        savedUser.setIdUser(100);
    }

    @Nested
    @DisplayName("Flujo exitoso")
    class SuccessfulFlow {

        @Test
        @DisplayName("debería registrar usuario exitosamente")
        void shouldRegisterUserSuccessfully() {
            // Given
            when(recaptchaPort.verifyToken("recaptcha-token", "Register")).thenReturn(true);
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);

            // When
            User result = useCase.handle(validCommand);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);
            assertThat(result.getEmail()).isEqualTo("newuser@example.com");
            assertThat(result.getUserName()).isEqualTo("Nuevo Usuario");
            assertThat(result.getRole()).isEqualTo(Role.CLIENT);

            verify(recaptchaPort).verifyToken("recaptcha-token", "Register");
            verify(userRepository).existsByEmail("newuser@example.com");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).saveUser(userCaptor.capture());
            verify(emailPort).sendWelcomeEmail("newuser@example.com", "Nuevo Usuario");

            User captured = userCaptor.getValue();
            assertThat(captured.getPassword()).isEqualTo("$2a$10$hashedPassword");
        }

        @Test
        @DisplayName("no debe romper si el envío de email falla")
        void shouldNotFailIfWelcomeEmailFails() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);
            doThrow(new RuntimeException("SMTP error"))
                    .when(emailPort).sendWelcomeEmail(anyString(), anyString());

            // When
            User result = useCase.handle(validCommand);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getIdUser()).isEqualTo(100);
            verify(emailPort).sendWelcomeEmail("newuser@example.com", "Nuevo Usuario");
        }
    }

    @Nested
    @DisplayName("Validación de reCAPTCHA")
    class RecaptchaValidation {

        @Test
        @DisplayName("debería lanzar InvalidRecaptchaException cuando el token es inválido")
        void shouldThrowInvalidRecaptchaExceptionWhenTokenIsInvalid() {
            // Given
            when(recaptchaPort.verifyToken("recaptcha-token", "Register")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(InvalidRecaptchaException.class)
                    .hasMessageContaining("Verificación de seguridad falló");

            verify(userRepository, never()).existsByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Email duplicado")
    class DuplicateEmail {

        @Test
        @DisplayName("debería lanzar UserAlreadyExistsException cuando el email ya existe")
        void shouldThrowUserAlreadyExistsExceptionWhenEmailExists() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.existsByEmail("newuser@example.com")).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("El email ya está registrado");

            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).saveUser(any());
            verify(emailPort, never()).sendWelcomeEmail(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Hash de contraseña")
    class PasswordHashing {

        @Test
        @DisplayName("debería hashear la contraseña antes de guardar")
        void shouldHashPasswordBeforeSaving() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(passwordEncoder).encode("password123");
            verify(userRepository).saveUser(userCaptor.capture());
            User captured = userCaptor.getValue();
            assertThat(captured.getPassword()).isEqualTo("$2a$10$hashedPassword");
        }
    }
}
