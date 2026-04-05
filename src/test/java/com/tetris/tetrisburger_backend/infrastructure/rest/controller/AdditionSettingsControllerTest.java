package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.GetAdditionSettings;
import com.tetris.tetrisburger_backend.domain.port.in.additionsettings.UpdateAdditionSettings;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AdditionSettingsDtoMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdditionSettingsController.class)
class AdditionSettingsControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetAdditionSettings getAdditionSettings;
    @MockitoBean private UpdateAdditionSettings updateAdditionSettings;
    @MockitoBean private AdditionSettingsDtoMapper mapper;

    @Nested
    class SecurityTests {

        @Test
        void adminEndpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(getAdditionSettings);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void adminEndpoint_shouldReturn403_whenRoleIsClient() throws Exception {
            mockMvc.perform(get("/api/addition-settings"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void adminEndpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            mockMvc.perform(get("/api/addition-settings"))
                    .andExpect(status().isOk());
        }
    }
}
