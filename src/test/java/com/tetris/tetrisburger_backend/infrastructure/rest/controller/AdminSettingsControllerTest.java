package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.*;
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

import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class AdminSettingsControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private GetBurgerSettings getBurgerSettings;
    @Mock private UpdateBurgerSettings updateBurgerSettings;

    @InjectMocks private AdminSettingsController controller;

    private BurgerSettings mockSettings;
    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        mockUserDetails = mock(CustomUserDetails.class);
        lenient().when(mockUserDetails.getId()).thenReturn(1);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                    }
                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockUserDetails;
                    }
                })
                .build();
        mockSettings = mock(BurgerSettings.class);
    }

    @Nested
    class GetBurgerSettingsTests {
        @Test
        void shouldGetSettingsAndReturn200() throws Exception {
            when(getBurgerSettings.handle()).thenReturn(mockSettings);

            mockMvc.perform(get("/api/admin/settings/burgers"))
                    .andExpect(status().isOk());

            verify(getBurgerSettings).handle();
        }
    }

    @Nested
    class UpdateBurgerSettingsTests {
        @Test
        void shouldUpdateSettingsAndReturn200() throws Exception {
            UpdateBurgerSettingsRequestDTO request = new UpdateBurgerSettingsRequestDTO(
                java.math.BigDecimal.ZERO, java.math.BigDecimal.TEN, 1, 5, true
            );

            when(updateBurgerSettings.handle(any())).thenReturn(mockSettings);

            mockMvc.perform(put("/api/admin/settings/burgers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(updateBurgerSettings).handle(any());
        }
    }
}
