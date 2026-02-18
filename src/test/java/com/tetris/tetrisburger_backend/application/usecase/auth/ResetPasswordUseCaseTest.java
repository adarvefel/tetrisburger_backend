package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ResetPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import com.tetris.tetrisburger_backend.domain.port.out.TokenPort;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ResetPasswordUseCase")
class ResetPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenPort tokenPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RecaptchaPort recaptchaPort;

    @InjectMocks
    private ResetPasswordUseCase useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private ResetPasswordCommand validCommand;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validCommand = new ResetPasswordCommand(
                "reset-token-123",
                "newPassword123"
        );

        existingUser = User.createClient(
                "Test User",
                "user@example.com",
                "$2a$10$oldHashedPassword"
        );
        existingUser.setIdUser(10);
    }

    @Nested
    @DisplayName("Flujo exitoso")
    class SuccessfulFlow {

        @Test
        @DisplayName("debería resetear contraseña exitosamente")
        void shouldResetPasswordSuccessfully() {
            // Given
            when(tokenPort.validatePasswordResetToken("reset-token-123")).thenReturn(true);
            when(tokenPort.extractEmailFromPasswordResetToken("reset-token-123"))
                    .thenReturn("user@example.com");
            when(userRepository.findUserByEmail("user@example.com"))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(tokenPort).validatePasswordResetToken("reset-token-123");
            verify(tokenPort).extractEmailFromPasswordResetToken("reset-token-123");
            verify(userRepository).findUserByEmail("user@example.com");
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).saveUser(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
            assertThat(savedUser.getEmail()).isEqualTo("user@example.com");
            assertThat(savedUser.getUserName()).isEqualTo("Test User");
        }

        @Test
        @DisplayName("debería hashear la nueva contraseña antes de guardar")
        void shouldHashNewPasswordBeforeSaving() {
            // Given
            when(tokenPort.validatePasswordResetToken(anyString())).thenReturn(true);
            when(tokenPort.extractEmailFromPasswordResetToken(anyString()))
                    .thenReturn("user@example.com");
            when(userRepository.findUserByEmail(anyString()))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).saveUser(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
        }

        @Test
        @DisplayName("debería preservar todos los demás campos del usuario")
        void shouldPreserveOtherUserFields() {
            // Given
            User userWithDetails = User.createClient(
                    "Detailed User",
                    "detailed@example.com",
                    "$2a$10$oldPassword"
            );
            userWithDetails.setIdUser(20);

            when(tokenPort.validatePasswordResetToken(anyString())).thenReturn(true);
            when(tokenPort.extractEmailFromPasswordResetToken(anyString()))
                    .thenReturn("detailed@example.com");
            when(userRepository.findUserByEmail("detailed@example.com"))
                    .thenReturn(Optional.of(userWithDetails));
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(userWithDetails);

            // When
            useCase.handle(validCommand);

            // Then
            verify(userRepository).saveUser(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getIdUser()).isEqualTo(20);
            assertThat(savedUser.getUserName()).isEqualTo("Detailed User");
            assertThat(savedUser.getEmail()).isEqualTo("detailed@example.com");
            assertThat(savedUser.getRole()).isEqualTo(userWithDetails.getRole());
        }
    }

    @Nested
    @DisplayName("Validación de token")
    class TokenValidation {

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando el token es inválido")
        void shouldThrowInvalidTokenWhenTokenInvalid() {
            // Given
            when(tokenPort.validatePasswordResetToken("reset-token-123")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token inválido o expirado");

            verify(tokenPort, never()).extractEmailFromPasswordResetToken(anyString());
            verify(userRepository, never()).findUserByEmail(anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).saveUser(any());
        }

        @Test
        @DisplayName("debería lanzar InvalidTokenException cuando el token expiró")
        void shouldThrowInvalidTokenWhenTokenExpired() {
            // Given
            when(tokenPort.validatePasswordResetToken("reset-token-123")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(InvalidTokenException.class)
                    .hasMessage("Token inválido o expirado");
        }
    }

    @Nested
    @DisplayName("Usuario no encontrado")
    class UserNotFound {

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando el usuario no existe")
        void shouldThrowUserNotFoundWhenUserDoesNotExist() {
            // Given
            when(tokenPort.validatePasswordResetToken(anyString())).thenReturn(true);
            when(tokenPort.extractEmailFromPasswordResetToken(anyString()))
                    .thenReturn("nonexistent@example.com");
            when(userRepository.findUserByEmail("nonexistent@example.com"))
                    .thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");

            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).saveUser(any());
        }
    }

    @Nested
    @DisplayName("Extracción de email del token")
    class EmailExtraction {

        @Test
        @DisplayName("debería extraer email del token correctamente")
        void shouldExtractEmailFromToken() {
            // Given
            when(tokenPort.validatePasswordResetToken(anyString())).thenReturn(true);
            when(tokenPort.extractEmailFromPasswordResetToken("reset-token-123"))
                    .thenReturn("extracted@example.com");

            User userFromToken = User.createClient(
                    "Extracted User",
                    "extracted@example.com",
                    "$2a$10$oldPassword"
            );

            when(userRepository.findUserByEmail("extracted@example.com"))
                    .thenReturn(Optional.of(userFromToken));
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(userFromToken);

            // When
            useCase.handle(validCommand);

            // Then
            verify(tokenPort).extractEmailFromPasswordResetToken("reset-token-123");
            verify(userRepository).findUserByEmail("extracted@example.com");
        }
    }

    @Nested
    @DisplayName("Flujo / Orden de llamadas")
    class FlowOrder {

        @Test
        @DisplayName("debería seguir el orden: validate -> extract -> findUser -> encode -> save")
        void shouldFollowCorrectCallOrder() {
            // Given
            when(tokenPort.validatePasswordResetToken(anyString())).thenReturn(true);
            when(tokenPort.extractEmailFromPasswordResetToken(anyString()))
                    .thenReturn("user@example.com");
            when(userRepository.findUserByEmail(anyString()))
                    .thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newHashedPassword");
            when(userRepository.saveUser(any(User.class))).thenReturn(existingUser);

            // When
            useCase.handle(validCommand);

            // Then
            var inOrder = inOrder(tokenPort, userRepository, passwordEncoder);
            inOrder.verify(tokenPort).validatePasswordResetToken("reset-token-123");
            inOrder.verify(tokenPort).extractEmailFromPasswordResetToken("reset-token-123");
            inOrder.verify(userRepository).findUserByEmail("user@example.com");
            inOrder.verify(passwordEncoder).encode("newPassword123");
            inOrder.verify(userRepository).saveUser(any(User.class));
        }
    }
}
