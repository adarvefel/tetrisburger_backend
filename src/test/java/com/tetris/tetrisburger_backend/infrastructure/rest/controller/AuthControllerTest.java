package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ForgotPassword;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginUser;
import com.tetris.tetrisburger_backend.domain.port.in.auth.LoginWithGoogle;
import com.tetris.tetrisburger_backend.domain.port.in.auth.ResetPassword;
import com.tetris.tetrisburger_backend.domain.port.in.user.RegisterUser;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.auth.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AuthRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.UserRestDtoMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    class LoginTests {
        @Test
        void shouldLoginAndReturn200() throws Exception {
            when(authMapper.toLoginCommand(any())).thenReturn(mock());
            when(loginUser.execute(any())).thenReturn(mock());

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isOk());

            verify(loginUser).execute(any());
        }
    }

    @Nested
    class RegisterTests {
        @Test
        void shouldRegisterAndReturn201() throws Exception {
            when(authMapper.toRegisterCommand(any())).thenReturn(mock());
            when(registerUser.handle(any())).thenReturn(mock());

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isCreated());

            verify(registerUser).handle(any());
        }
    }
}
