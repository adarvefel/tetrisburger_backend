package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.LoginResponse;
import com.tetris.tetrisburger_backend.domain.exception.InvalidCredentialsException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.User;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ForgotPassword;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginUser;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginWithGoogle;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ResetPassword;
import com.tetris.tetrisburger_backend.domain.port.in.user.RegisterUser;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.user.RegisterUserResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AuthRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private LoginUser loginUser;
    @Mock private RegisterUser registerUser;
    @Mock private LoginWithGoogle loginWithGoogle;
    @Mock private ForgotPassword forgotPassword;
    @Mock private ResetPassword resetPassword;
    @Mock private AuthRestDtoMapper authMapper;
    @Mock private UserRestDtoMapper userMapper;

    @InjectMocks private AuthController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(
                        new ValidationExceptionHandler(),
                        new BurgerExceptionHandler(),
                        new ProductExceptionHandler(),
                        new GlobalExceptionHandler()
                )
                .build();
    }

    @Nested
    class RegisterTests {
        @Test
        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
            RegisterUserRequestDTO req = new RegisterUserRequestDTO("usuario1", "u@test.com", "secret12", null);
            User user = mock(User.class);
            RegisterUserResponseDTO res = new RegisterUserResponseDTO(1, "usuario1", "u@test.com", LocalDateTime.now());

            when(authMapper.toRegisterCommand(any())).thenReturn(mock());
            when(registerUser.handle(any())).thenReturn(user);
            when(authMapper.toRegisterResponseDTO(user)).thenReturn(res);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idUser").value(1))
                    .andExpect(jsonPath("$.userName").value("usuario1"));

            verify(registerUser, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenInvalidRequest() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(registerUser);
        }
    }

    @Nested
    class LoginTests {
        @Test
        void shouldReturn200WhenLoginSuccessful() throws Exception {
            LoginRequestDTO req = new LoginRequestDTO("a@b.com", "pass1234", null);
            LoginResponse domain = mock(LoginResponse.class);
            LoginResponseDTO dto = new LoginResponseDTO("tok", "Bearer", 3600L, null, LocalDateTime.now());

            when(authMapper.toLoginCommand(any())).thenReturn(mock());
            when(loginUser.execute(any())).thenReturn(domain);
            when(authMapper.toLoginResponseDTO(domain)).thenReturn(dto);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("tok"));

            verify(loginUser, times(1)).execute(any());
        }

        @Test
        void shouldReturn401WhenInvalidCredentials() throws Exception {
            LoginRequestDTO req = new LoginRequestDTO("a@b.com", "bad", null);
            when(authMapper.toLoginCommand(any())).thenReturn(mock());
            when(loginUser.execute(any())).thenThrow(new InvalidCredentialsException("Credenciales inválidas"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());

            verify(loginUser, times(1)).execute(any());
        }

        @Test
        void shouldReturn400WhenBodyInvalid() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"\",\"password\":\"\"}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(loginUser);
        }
    }

    @Nested
    class LoginWithGoogleTests {
        @Test
        void shouldReturn200WhenSuccessful() throws Exception {
            GoogleLoginRequestDTO req = new GoogleLoginRequestDTO("google-token");
            LoginResponse domain = mock(LoginResponse.class);
            LoginResponseDTO dto = new LoginResponseDTO("tok", "Bearer", 3600L, null, LocalDateTime.now());

            when(authMapper.toLoginWithGoogleCommand(any())).thenReturn(mock());
            when(loginWithGoogle.handle(any())).thenReturn(domain);
            when(authMapper.toLoginResponseDTO(domain)).thenReturn(dto);

            mockMvc.perform(post("/api/auth/google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());

            verify(loginWithGoogle, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenTokenBlank() throws Exception {
            mockMvc.perform(post("/api/auth/google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"token\":\"\"}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(loginWithGoogle);
        }
    }

    @Nested
    class ForgotPasswordTests {
        @Test
        void shouldReturn200WhenSuccessful() throws Exception {
            ForgotPasswordRequestDTO req = new ForgotPasswordRequestDTO("user@test.com", null);
            when(authMapper.toForgotPasswordCommand(any())).thenReturn(mock());
            when(forgotPassword.execute(any())).thenReturn("token");

            mockMvc.perform(post("/api/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(forgotPassword, times(1)).execute(any());
        }

        @Test
        void shouldReturn400WhenEmailInvalid() throws Exception {
            mockMvc.perform(post("/api/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"not-an-email\"}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(forgotPassword);
        }

        @Test
        void shouldReturn404WhenUserNotFoundIfUseCaseThrows() throws Exception {
            ForgotPasswordRequestDTO req = new ForgotPasswordRequestDTO("ghost@test.com", null);
            when(authMapper.toForgotPasswordCommand(any())).thenReturn(mock());
            when(forgotPassword.execute(any())).thenThrow(new UserNotFoundException("Usuario no encontrado"));

            mockMvc.perform(post("/api/auth/forgot-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound());

            verify(forgotPassword, times(1)).execute(any());
        }
    }

    @Nested
    class ResetPasswordTests {
        @Test
        void shouldReturn200WhenSuccessful() throws Exception {
            ResetPasswordRequestDTO req = new ResetPasswordRequestDTO("token123", "newpass12");
            when(authMapper.toResetPasswordCommand(any())).thenReturn(mock());
            doNothing().when(resetPassword).handle(any());

            mockMvc.perform(post("/api/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(resetPassword, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenPasswordTooShort() throws Exception {
            mockMvc.perform(post("/api/auth/reset-password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"token\":\"abc\",\"newPassword\":\"12345\"}"))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(resetPassword);
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void publicEndpoints_shouldBeAccessibleWithoutAuthentication() throws Exception {
            SecurityContextHolder.clearContext();
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
            verifyNoInteractions(loginUser);
        }
    }
}
