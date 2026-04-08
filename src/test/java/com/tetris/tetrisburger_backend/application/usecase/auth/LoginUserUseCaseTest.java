package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.exception.InvalidCredentialsException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidRecaptchaException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginUserCommand;
import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import com.tetris.tetrisburger_backend.domain.port.out.TokenPort;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de LoginUserUseCase")
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenPort tokenPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RecaptchaPort recaptchaPort;

    @InjectMocks
    private LoginUserUseCase useCase;

    private LoginUserCommand validCommand;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validCommand = new LoginUserCommand(
                "user@example.com",
                "password123",
                "recaptcha-token"
        );

        existingUser = User.createClient(
                "Test User",
                "user@example.com",
                "$2a$10$hashedPassword"
        );
        existingUser.setIdUser(10);
    }

    @Nested
    @DisplayName("Login exitoso")
    class SuccessfulLogin {

        @Test
        @DisplayName("debería autenticar usuario con credenciales válidas")
        void shouldLoginWithValidCredentials() {
            // Given
            when(recaptchaPort.verifyToken("recaptcha-token", "Login")).thenReturn(true);
            when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);
            when(tokenPort.generateToken("user@example.com")).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            LoginResponse response = useCase.execute(validCommand);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo("jwt-token");
            assertThat(response.user()).isEqualTo(existingUser);
            assertThat(response.expiresIn()).isEqualTo(3600L);

            verify(recaptchaPort).verifyToken("recaptcha-token", "Login");
            verify(userRepository).findUserByEmail("user@example.com");
            verify(passwordEncoder).matches("password123", "$2a$10$hashedPassword");
            verify(tokenPort).generateToken("user@example.com");
            verify(tokenPort).getExpirationTime();
        }
    }

    @Nested
    @DisplayName("Validación de reCAPTCHA")
    class RecaptchaValidation {

        @Test
        @DisplayName("debería lanzar InvalidRecaptchaException cuando el token es inválido")
        void shouldThrowInvalidRecaptchaWhenTokenInvalid() {
            // Given
            when(recaptchaPort.verifyToken("recaptcha-token", "Login")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> useCase.execute(validCommand))
                    .isInstanceOf(InvalidRecaptchaException.class)
                    .hasMessageContaining("Verificación de seguridad falló");

            verify(userRepository, never()).findUserByEmail(anyString());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(tokenPort, never()).generateToken(anyString());
        }
    }

    @Nested
    @DisplayName("Validación de usuario")
    class UserValidation {

        @Test
        @DisplayName("debería lanzar InvalidCredentialsException cuando usuario no existe")
        void shouldThrowInvalidCredentialsWhenUserNotFound() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.execute(validCommand))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Credenciales inválidas");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(tokenPort, never()).generateToken(anyString());
        }
    }

    @Nested
    @DisplayName("Validación de contraseña")
    class PasswordValidation {

        @Test
        @DisplayName("debería lanzar InvalidCredentialsException cuando la contraseña es incorrecta")
        void shouldThrowInvalidCredentialsWhenPasswordInvalid() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> useCase.execute(validCommand))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Credenciales inválidas");

            verify(tokenPort, never()).generateToken(anyString());
        }
    }

    @Nested
    @DisplayName("Flujo / Orden de llamadas")
    class FlowOrder {

        @Test
        @DisplayName("debería seguir el orden: reCAPTCHA -> repo -> password -> token")
        void shouldFollowCorrectCallOrder() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(tokenPort.generateToken(anyString())).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            useCase.execute(validCommand);

            // Then
            var inOrder = inOrder(recaptchaPort, userRepository, passwordEncoder, tokenPort);
            inOrder.verify(recaptchaPort).verifyToken("recaptcha-token", "Login");
            inOrder.verify(userRepository).findUserByEmail("user@example.com");
            inOrder.verify(passwordEncoder).matches("password123", "$2a$10$hashedPassword");
            inOrder.verify(tokenPort).generateToken("user@example.com");
            inOrder.verify(tokenPort).getExpirationTime();
        }
    }
}
