package com.tetris.tetrisburger_backend.application.usecase.auth;

import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.exception.InvalidCredentialsException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidTokenException;
import com.tetris.tetrisburger_backend.domain.model.Role;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.command.LoginWithGoogleCommand;
import com.tetris.tetrisburger_backend.domain.port.out.EmailPort;
import com.tetris.tetrisburger_backend.domain.port.out.GoogleAuthPort;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de LoginWithGoogleUseCase")
class LoginWithGoogleUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoogleAuthPort googleAuthPort;

    @Mock
    private TokenPort tokenPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailPort emailPort;

    @InjectMocks
    private LoginWithGoogleUseCase useCase;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private LoginWithGoogleCommand validCommand;
    private Map<String, String> googleUserInfo;
    private User existingUser;

    @BeforeEach
    void setUp() {
        validCommand = new LoginWithGoogleCommand("google-token-123");

        googleUserInfo = new HashMap<>();
        googleUserInfo.put("email", "user@gmail.com");
        googleUserInfo.put("userName", "Google User");

        existingUser = User.createClient(
                "Google User",
                "user@gmail.com",
                "$2a$10$hashedRandomPassword"
        );
        existingUser.setIdUser(50);
    }

    @Nested
    @DisplayName("Login exitoso con usuario existente")
    class ExistingUserLogin {

        @Test
        @DisplayName("debería autenticar usuario existente desde Google")
        void shouldLoginExistingGoogleUser() {
            // Given
            when(googleAuthPort.validateAndExtractUserInfo("google-token-123"))
                    .thenReturn(googleUserInfo);
            when(userRepository.findUserByEmail("user@gmail.com"))
                    .thenReturn(Optional.of(existingUser));
            when(tokenPort.generateToken("user@gmail.com")).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            LoginResponse response = useCase.handle(validCommand);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo("jwt-token");
            assertThat(response.user()).isEqualTo(existingUser);
            assertThat(response.expiresIn()).isEqualTo(3600L);

            verify(googleAuthPort).validateAndExtractUserInfo("google-token-123");
            verify(userRepository).findUserByEmail("user@gmail.com");
            verify(userRepository, never()).saveUser(any()); // No crea nuevo usuario
            verify(tokenPort).generateToken("user@gmail.com");
            verify(emailPort, never()).sendWelcomeEmail(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Registro automático de nuevo usuario")
    class NewUserRegistration {

        @Test
        @DisplayName("debería crear nuevo usuario cuando no existe en BD")
        void shouldCreateNewUserWhenNotExists() {
            // Given
            User newUser = User.createClient(
                    "Google User",
                    "user@gmail.com",
                    "$2a$10$randomHashed"
            );
            newUser.setIdUser(100);

            when(googleAuthPort.validateAndExtractUserInfo("google-token-123"))
                    .thenReturn(googleUserInfo);
            when(userRepository.findUserByEmail("user@gmail.com"))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$randomHashed");
            when(userRepository.saveUser(any(User.class))).thenReturn(newUser);
            when(tokenPort.generateToken("user@gmail.com")).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            LoginResponse response = useCase.handle(validCommand);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.token()).isEqualTo("jwt-token");
            assertThat(response.user().getEmail()).isEqualTo("user@gmail.com");
            assertThat(response.user().getUserName()).isEqualTo("Google User");
            assertThat(response.user().getRole()).isEqualTo(Role.CLIENT);

            verify(userRepository).saveUser(userCaptor.capture());
            verify(emailPort).sendWelcomeEmail("user@gmail.com", "Google User");

            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getEmail()).isEqualTo("user@gmail.com");
            assertThat(capturedUser.getUserName()).isEqualTo("Google User");
            assertThat(capturedUser.getPassword()).isNotBlank();
        }

        @Test
        @DisplayName("debería generar contraseña aleatoria para nuevo usuario de Google")
        void shouldGenerateRandomPasswordForNewGoogleUser() {
            // Given
            User newUser = User.createClient(
                    "Google User",
                    "user@gmail.com",
                    "$2a$10$randomHashed"
            );
            newUser.setIdUser(100);

            when(googleAuthPort.validateAndExtractUserInfo(anyString()))
                    .thenReturn(googleUserInfo);
            when(userRepository.findUserByEmail(anyString()))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$randomHashed");
            when(userRepository.saveUser(any(User.class))).thenReturn(newUser);
            when(tokenPort.generateToken(anyString())).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            useCase.handle(validCommand);

            // Then
            verify(passwordEncoder).encode(argThat(pwd -> pwd != null && pwd.length() > 0));
            verify(userRepository).saveUser(userCaptor.capture());
            User captured = userCaptor.getValue();
            assertThat(captured.getPassword()).isEqualTo("$2a$10$randomHashed");
        }

        @Test
        @DisplayName("no debería fallar si el envío de email falla")
        void shouldNotFailIfWelcomeEmailFails() {
            // Given
            User newUser = User.createClient(
                    "Google User",
                    "user@gmail.com",
                    "$2a$10$randomHashed"
            );
            newUser.setIdUser(100);

            when(googleAuthPort.validateAndExtractUserInfo(anyString()))
                    .thenReturn(googleUserInfo);
            when(userRepository.findUserByEmail(anyString()))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$randomHashed");
            when(userRepository.saveUser(any(User.class))).thenReturn(newUser);
            when(tokenPort.generateToken(anyString())).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);
            doThrow(new RuntimeException("SMTP error"))
                    .when(emailPort).sendWelcomeEmail(anyString(), anyString());

            // When
            LoginResponse response = useCase.handle(validCommand);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.user().getIdUser()).isEqualTo(100);
            verify(emailPort).sendWelcomeEmail("user@gmail.com", "Google User");
        }
    }

    @Nested
    @DisplayName("Validación de token de Google")
    class GoogleTokenValidation {

        @Test
        @DisplayName("debería lanzar InvalidCredentialsException cuando el token de Google es inválido")
        void shouldThrowInvalidCredentialsWhenGoogleTokenInvalid() {
            // Given
            when(googleAuthPort.validateAndExtractUserInfo("google-token-123"))
                    .thenThrow(new InvalidTokenException("Token inválido"));

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Autenticación con Google fallida");

            verify(userRepository, never()).findUserByEmail(anyString());
            verify(tokenPort, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("debería lanzar InvalidCredentialsException cuando Google devuelve error")
        void shouldThrowInvalidCredentialsWhenGoogleReturnsError() {
            // Given
            when(googleAuthPort.validateAndExtractUserInfo(anyString()))
                    .thenThrow(new InvalidTokenException("Google OAuth error"));

            // When / Then
            assertThatThrownBy(() -> useCase.handle(validCommand))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Autenticación con Google fallida");
        }
    }

    @Nested
    @DisplayName("Extracción de información de Google")
    class GoogleUserInfoExtraction {

        @Test
        @DisplayName("debería extraer email y userName de la respuesta de Google")
        void shouldExtractEmailAndUserNameFromGoogleResponse() {
            // Given
            Map<String, String> customUserInfo = new HashMap<>();
            customUserInfo.put("email", "custom@gmail.com");
            customUserInfo.put("userName", "Custom Name");

            User savedUser = User.createClient(
                    "Custom Name",
                    "custom@gmail.com",
                    "$2a$10$hashed"
            );
            savedUser.setIdUser(200);

            when(googleAuthPort.validateAndExtractUserInfo(anyString()))
                    .thenReturn(customUserInfo);
            when(userRepository.findUserByEmail("custom@gmail.com"))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
            when(userRepository.saveUser(any(User.class))).thenReturn(savedUser);
            when(tokenPort.generateToken("custom@gmail.com")).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            LoginResponse response = useCase.handle(validCommand);

            // Then
            assertThat(response.user().getEmail()).isEqualTo("custom@gmail.com");
            assertThat(response.user().getUserName()).isEqualTo("Custom Name");
        }
    }

    @Nested
    @DisplayName("Generación de JWT")
    class JWTGeneration {

        @Test
        @DisplayName("debería generar JWT con el email del usuario")
        void shouldGenerateJWTWithUserEmail() {
            // Given
            when(googleAuthPort.validateAndExtractUserInfo(anyString()))
                    .thenReturn(googleUserInfo);
            when(userRepository.findUserByEmail("user@gmail.com"))
                    .thenReturn(Optional.of(existingUser));
            when(tokenPort.generateToken("user@gmail.com")).thenReturn("jwt-token-12345");
            when(tokenPort.getExpirationTime()).thenReturn(7200L);

            // When
            LoginResponse response = useCase.handle(validCommand);

            // Then
            assertThat(response.token()).isEqualTo("jwt-token-12345");
            assertThat(response.expiresIn()).isEqualTo(7200L);
            verify(tokenPort).generateToken("user@gmail.com");
        }
    }

    @Nested
    @DisplayName("Flujo / Orden de llamadas")
    class FlowOrder {

        @Test
        @DisplayName("debería seguir el orden correcto para usuario existente")
        void shouldFollowCorrectOrderForExistingUser() {
            // Given
            when(googleAuthPort.validateAndExtractUserInfo(anyString()))
                    .thenReturn(googleUserInfo);
            when(userRepository.findUserByEmail(anyString()))
                    .thenReturn(Optional.of(existingUser));
            when(tokenPort.generateToken(anyString())).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            useCase.handle(validCommand);

            // Then
            var inOrder = inOrder(googleAuthPort, userRepository, tokenPort);
            inOrder.verify(googleAuthPort).validateAndExtractUserInfo("google-token-123");
            inOrder.verify(userRepository).findUserByEmail("user@gmail.com");
            inOrder.verify(tokenPort).generateToken("user@gmail.com");
            inOrder.verify(tokenPort).getExpirationTime();
        }

        @Test
        @DisplayName("debería seguir el orden correcto para nuevo usuario")
        void shouldFollowCorrectOrderForNewUser() {
            // Given
            User newUser = User.createClient(
                    "Google User",
                    "user@gmail.com",
                    "$2a$10$hashed"
            );
            newUser.setIdUser(100);

            when(googleAuthPort.validateAndExtractUserInfo(anyString()))
                    .thenReturn(googleUserInfo);
            when(userRepository.findUserByEmail(anyString()))
                    .thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");
            when(userRepository.saveUser(any(User.class))).thenReturn(newUser);
            when(tokenPort.generateToken(anyString())).thenReturn("jwt-token");
            when(tokenPort.getExpirationTime()).thenReturn(3600L);

            // When
            useCase.handle(validCommand);

            // Then
            var inOrder = inOrder(googleAuthPort, userRepository, passwordEncoder, tokenPort, emailPort);
            inOrder.verify(googleAuthPort).validateAndExtractUserInfo("google-token-123");
            inOrder.verify(userRepository).findUserByEmail("user@gmail.com");
            inOrder.verify(passwordEncoder).encode(anyString());
            inOrder.verify(userRepository).saveUser(any(User.class));
            inOrder.verify(emailPort).sendWelcomeEmail("user@gmail.com", "Google User");
            inOrder.verify(tokenPort).generateToken("user@gmail.com");
            inOrder.verify(tokenPort).getExpirationTime();
        }
    }
}
