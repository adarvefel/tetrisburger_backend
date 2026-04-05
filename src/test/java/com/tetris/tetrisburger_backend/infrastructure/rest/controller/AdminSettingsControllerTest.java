package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.exception.InvalidSettingsException;
import com.tetris.tetrisburger_backend.domain.model.BurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.GetBurgerSettings;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateBurgerSettings;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.UpdateBurgerSettingsRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminSettingsControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private GetBurgerSettings getBurgerSettings;
    @Mock private UpdateBurgerSettings updateBurgerSettings;

    @InjectMocks private AdminSettingsController controller;

    private CustomUserDetails mockUserDetails;

    private void mockAuthenticatedUser(Long userId) {
        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
    }

    private UpdateBurgerSettingsRequestDTO validUpdateDto() {
        return new UpdateBurgerSettingsRequestDTO(
                new BigDecimal("10000.00"),
                new BigDecimal("50000.00"),
                2,
                10,
                true
        );
    }

    @BeforeEach
    void setUp() {
        mockUserDetails = mock(CustomUserDetails.class);
        mockAuthenticatedUser(1L);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(
                        new ValidationExceptionHandler(),
                        new BurgerExceptionHandler(),
                        new ProductExceptionHandler(),
                        new GlobalExceptionHandler()
                )
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return mockUserDetails;
                    }
                })
                .build();
    }

    @Nested
    class GetBurgerSettingsTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            BurgerSettings settings = BurgerSettings.createDefaults();
            when(getBurgerSettings.handle()).thenReturn(settings);

            mockMvc.perform(get("/api/admin/settings/burgers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.minIngredients").value(2));

            verify(getBurgerSettings, times(1)).handle();
        }
    }

    @Nested
    class UpdateBurgerSettingsTests {
        @Test
        void shouldReturn200WhenUpdatedSuccessfully() throws Exception {
            BurgerSettings updated = BurgerSettings.createDefaults();
            when(updateBurgerSettings.handle(any())).thenReturn(updated);

            mockMvc.perform(put("/api/admin/settings/burgers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateDto())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customBurgersEnabled").value(true));

            verify(updateBurgerSettings, times(1)).handle(any());
        }

        @Test
        void shouldReturn400WhenInvalidRequest() throws Exception {
            String invalid = "{\"customBurgerMinPrice\":null,\"customBurgerMaxPrice\":50000,\"minIngredients\":2,\"maxIngredients\":10,\"customBurgersEnabled\":true}";
            mockMvc.perform(put("/api/admin/settings/burgers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalid))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(updateBurgerSettings);
        }

        @Test
        void shouldReturn400WhenUseCaseRejects() throws Exception {
            when(updateBurgerSettings.handle(any())).thenThrow(new InvalidSettingsException("El precio mínimo no puede ser mayor al máximo"));

            mockMvc.perform(put("/api/admin/settings/burgers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validUpdateDto())))
                    .andExpect(status().isBadRequest());

            verify(updateBurgerSettings, times(1)).handle(any());
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(getBurgerSettings);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
            mockMvc.perform(get("/api/admin/settings/burgers"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            BurgerSettings settings = BurgerSettings.createDefaults();
            when(getBurgerSettings.handle()).thenReturn(settings);
            mockMvc.perform(get("/api/admin/settings/burgers"))
                    .andExpect(status().isOk());
        }
    }
}
