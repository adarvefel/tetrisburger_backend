package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.model.Cart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.ClearCart;
import com.tetris.tetrisburger_backend.domain.port.in.cart.GetCart;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.CartRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock private AddCart addCart;
    @Mock private GetCart getCart;
    @Mock private ClearCart clearCart;
    @Mock private CartRestDtoMapper mapper;
    @Mock private CustomUserDetails mockUserDetails;

    @InjectMocks
    private CartController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Integer USER_ID = 42;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        // Fix 1 — lenient stubs en @BeforeEach
        lenient().when(mockUserDetails.getId()).thenReturn(USER_ID);

        // Fix 2 — SecurityContextHolder para @AuthenticationPrincipal
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, List.of())
        );
        SecurityContextHolder.setContext(ctx);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── POST /api/cart/sync ─────────────────────────────────────────────────

    @Test
    void sync_returnsOk_withMappedCartItems() throws Exception {
        Cart domain = mock(Cart.class);
        CartItemResponseDTO responseDTO = mock(CartItemResponseDTO.class);

        when(mapper.toDomainList(any())).thenReturn(List.of());
        when(addCart.handle(eq(USER_ID), any())).thenReturn(domain);
        when(mapper.toResponseDTOList(domain)).thenReturn(List.of(responseDTO));

        mockMvc.perform(post("/api/cart/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());

        verify(addCart).handle(eq(USER_ID), any());
        verify(mapper).toResponseDTOList(domain);
    }

    @Test
    void sync_withItems_passesListToUseCase() throws Exception {
        Cart domain = mock(Cart.class);
        List<CartItemRequestDTO> requestItems = List.of(
                mock(CartItemRequestDTO.class),
                mock(CartItemRequestDTO.class)
        );
        String body = objectMapper.writeValueAsString(requestItems);

        when(mapper.toDomainList(any())).thenReturn(List.of());
        when(addCart.handle(eq(USER_ID), any())).thenReturn(domain);
        when(mapper.toResponseDTOList(domain)).thenReturn(List.of());

        mockMvc.perform(post("/api/cart/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(mapper).toDomainList(any());
        verify(addCart).handle(eq(USER_ID), any());
    }

    @Test
    void sync_withoutContentType_returnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/cart/sync")
                        .content("[]"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void sync_whenServiceThrows_propagatesException() {
        when(mapper.toDomainList(any())).thenReturn(List.of());
        when(addCart.handle(any(), any())).thenThrow(new RuntimeException("Sync failed"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/cart/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")));
    }

    // ─── GET /api/cart ───────────────────────────────────────────────────────

    @Test
    void get_returnsOk_withMappedCartItems() throws Exception {
        Cart domain = mock(Cart.class);
        CartItemResponseDTO responseDTO = mock(CartItemResponseDTO.class);

        when(getCart.handle(USER_ID)).thenReturn(domain);
        when(mapper.toResponseDTOList(domain)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk());

        verify(getCart).handle(USER_ID);
        verify(mapper).toResponseDTOList(domain);
    }

    @Test
    void get_returnsEmptyList_whenCartIsEmpty() throws Exception {
        Cart domain = mock(Cart.class);

        when(getCart.handle(USER_ID)).thenReturn(domain);
        when(mapper.toResponseDTOList(domain)).thenReturn(List.of());

        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk());

        verify(getCart).handle(USER_ID);
    }

    @Test
    void get_whenServiceThrows_propagatesException() {
        when(getCart.handle(USER_ID)).thenThrow(new RuntimeException("Not found"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/cart")));
    }

    // ─── DELETE /api/cart/clear ──────────────────────────────────────────────

    @Test
    void clear_returnsNoContent() throws Exception {
        doNothing().when(clearCart).handle(USER_ID);

        mockMvc.perform(delete("/api/cart/clear"))
                .andExpect(status().isNoContent());

        verify(clearCart).handle(USER_ID);
    }

    @Test
    void clear_whenServiceThrows_propagatesException() {
        doThrow(new RuntimeException("Clear failed")).when(clearCart).handle(USER_ID);

        assertThrows(Exception.class, () ->
                mockMvc.perform(delete("/api/cart/clear")));
    }
}