package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.port.in.payment.CreatePayment;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.PaymentRestDtoMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private CreatePayment createPayment;
    @MockitoBean private PaymentRestDtoMapper mapper;

    @Nested
    class SecurityTests {

        @Test
        void endpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createPayment);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void endpoint_shouldBeAccessible_whenRoleIsClient() throws Exception {
            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"idOrder\":1,\"paymentMethod\":\"CARD\"}")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void endpoint_shouldBeAccessible_whenRoleIsAdmin() throws Exception {
            mockMvc.perform(post("/api/payments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"idOrder\":1,\"paymentMethod\":\"CARD\"}")
                            .with(csrf()))
                    .andExpect(status().isCreated());
        }
    }
}
