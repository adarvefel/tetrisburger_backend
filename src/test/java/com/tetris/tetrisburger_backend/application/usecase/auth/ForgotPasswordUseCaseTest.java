package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.exception.InvalidRecaptchaException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.ForgotPasswordCommand;
import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de ForgotPasswordUseCase")
class ForgotPasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailPort emailPort;

    @Mock
    private TokenPort tokenPort;

    @Mock
    private RecaptchaPort recaptchaPort;

    @InjectMocks
    private ForgotPasswordUseCase useCase;

    @Captor
    private ArgumentCaptor<String> emailBodyCaptor;

    private ForgotPasswordCommand validCommand;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validCommand = new ForgotPasswordCommand(
                "user@example.com",
                "recaptcha-token"
        );

        existingUser = User.createClient(
                "Test User",
                "user@example.com",
                "$2a$10$hashedPassword"
        );
        existingUser.setIdUser(10);

        // Configurar frontendUrl
        ReflectionTestUtils.setField(useCase, "frontendUrl", "http://localhost:5173");
    }

    @Nested
    @DisplayName("Flujo exitoso")
    class SuccessfulFlow {

        @Test
        @DisplayName("debería generar token y enviar email de recuperación")
        void shouldGenerateTokenAndSendRecoveryEmail() {
            // Given
            when(recaptchaPort.verifyToken("recaptcha-token", "ForgotPassword")).thenReturn(true);
            when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
            when(tokenPort.generatePasswordResetToken("user@example.com", 3600000L))
                    .thenReturn("reset-token-123");

            // When
            String result = useCase.execute(validCommand);

            // Then
            assertThat(result).isEqualTo("user@example.com");

            verify(recaptchaPort).verifyToken("recaptcha-token", "ForgotPassword");
            verify(userRepository).findUserByEmail("user@example.com");
            verify(tokenPort).generatePasswordResetToken("user@example.com", 3600000L);
            verify(emailPort).sendEmail(
                    eq("user@example.com"),
                    eq("Recuperación de contraseña - TetrisBurger"),
                    emailBodyCaptor.capture()
            );

            String emailBody = emailBodyCaptor.getValue();
            assertThat(emailBody).contains("Hola Test User");
            assertThat(emailBody).contains("http://localhost:5173/reset-password?token=reset-token-123");
            assertThat(emailBody).contains("Este enlace expira en 1 hora");
        }

        @Test
        @DisplayName("debería usar frontendUrl configurada en el enlace")
        void shouldUseFrontendUrlInLink() {
            // Given
            ReflectionTestUtils.setField(useCase, "frontendUrl", "https://tetrisburger.com");

            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(existingUser));
            when(tokenPort.generatePasswordResetToken(anyString(), anyLong()))
                    .thenReturn("reset-token-456");

            // When
            useCase.execute(validCommand);

            // Then
            verify(emailPort).sendEmail(
                    eq("user@example.com"),
                    anyString(),
                    emailBodyCaptor.capture()
            );

            String emailBody = emailBodyCaptor.getValue();
            assertThat(emailBody).contains("https://tetrisburger.com/reset-password?token=reset-token-456");
        }

        @Test
        @DisplayName("debería generar token con tiempo de expiración de 1 hora")
        void shouldGenerateTokenWithOneHourExpiration() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(existingUser));
            when(tokenPort.generatePasswordResetToken(anyString(), anyLong()))
                    .thenReturn("reset-token");

            // When
            useCase.execute(validCommand);

            // Then
            verify(tokenPort).generatePasswordResetToken("user@example.com", 3600000L);
        }
    }

    @Nested
    @DisplayName("Validación de reCAPTCHA")
    class RecaptchaValidation {

        @Test
        @DisplayName("debería lanzar InvalidRecaptchaException cuando el token es inválido")
        void shouldThrowInvalidRecaptchaWhenTokenInvalid() {
            // Given
            when(recaptchaPort.verifyToken("recaptcha-token", "ForgotPassword")).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> useCase.execute(validCommand))
                    .isInstanceOf(InvalidRecaptchaException.class)
                    .hasMessageContaining("Verificación de seguridad falló");

            verify(userRepository, never()).findUserByEmail(anyString());
            verify(tokenPort, never()).generatePasswordResetToken(anyString(), anyLong());
            verify(emailPort, never()).sendEmail(anyString(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Usuario no encontrado")
    class UserNotFound {

        @Test
        @DisplayName("debería lanzar UserNotFoundException cuando el email no existe")
        void shouldThrowUserNotFoundWhenEmailDoesNotExist() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> useCase.execute(validCommand))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessage("Usuario no encontrado");

            verify(tokenPort, never()).generatePasswordResetToken(anyString(), anyLong());
            verify(emailPort, never()).sendEmail(anyString(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Contenido del email")
    class EmailContent {

        @Test
        @DisplayName("debería incluir nombre del usuario en el email")
        void shouldIncludeUserNameInEmail() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(existingUser));
            when(tokenPort.generatePasswordResetToken(anyString(), anyLong()))
                    .thenReturn("reset-token");

            // When
            useCase.execute(validCommand);

            // Then
            verify(emailPort).sendEmail(
                    anyString(),
                    anyString(),
                    emailBodyCaptor.capture()
            );

            String emailBody = emailBodyCaptor.getValue();
            assertThat(emailBody).contains("Hola Test User");
        }

        @Test
        @DisplayName("debería incluir instrucciones de seguridad en el email")
        void shouldIncludeSecurityInstructionsInEmail() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(existingUser));
            when(tokenPort.generatePasswordResetToken(anyString(), anyLong()))
                    .thenReturn("reset-token");

            // When
            useCase.execute(validCommand);

            // Then
            verify(emailPort).sendEmail(
                    anyString(),
                    anyString(),
                    emailBodyCaptor.capture()
            );

            String emailBody = emailBodyCaptor.getValue();
            assertThat(emailBody).contains("Si no solicitaste esto, ignora este correo");
        }
    }

    @Nested
    @DisplayName("Flujo / Orden de llamadas")
    class FlowOrder {

        @Test
        @DisplayName("debería seguir el orden: reCAPTCHA -> repo -> token -> email")
        void shouldFollowCorrectCallOrder() {
            // Given
            when(recaptchaPort.verifyToken(anyString(), anyString())).thenReturn(true);
            when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(existingUser));
            when(tokenPort.generatePasswordResetToken(anyString(), anyLong()))
                    .thenReturn("reset-token");

            // When
            useCase.execute(validCommand);

            // Then
            var inOrder = inOrder(recaptchaPort, userRepository, tokenPort, emailPort);
            inOrder.verify(recaptchaPort).verifyToken("recaptcha-token", "ForgotPassword");
            inOrder.verify(userRepository).findUserByEmail("user@example.com");
            inOrder.verify(tokenPort).generatePasswordResetToken("user@example.com", 3600000L);
            inOrder.verify(emailPort).sendEmail(
                    eq("user@example.com"),
                    eq("Recuperación de contraseña - TetrisBurger"),
                    anyString()
            );
        }
    }
}
