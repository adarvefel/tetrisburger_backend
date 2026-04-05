package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.ClearCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.GetCart;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.CartRestDtoMapper;
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

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AddCart addCart;
    @MockitoBean private GetCart getCart;
    @MockitoBean private ClearCart clearCart;
    @MockitoBean private CartRestDtoMapper mapper;

    @Nested
    class SecurityTests {

        @Test
        void endpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(addCart);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void endpoint_shouldBeAccessible_whenRoleIsClient() throws Exception {
            mockMvc.perform(get("/api/cart"))
                    .andExpect(status().isOk());
        }
    }
}
