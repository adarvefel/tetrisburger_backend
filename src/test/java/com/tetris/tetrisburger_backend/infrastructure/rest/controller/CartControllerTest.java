//package com.tetris.tetrisburger_backend.infrastructure.rest.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tetris.tetrisburger_backend.domain.enums.ItemType;
//import com.tetris.tetrisburger_backend.domain.model.Cart;
//import com.tetris.tetrisburger_backend.domain.model.CartItem;
//import com.tetris.tetrisburger_backend.domain.port.in.cart.AddCart;
//import com.tetris.tetrisburger_backend.domain.port.in.cart.ClearCart;
//import com.tetris.tetrisburger_backend.domain.port.in.cart.GetCart;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemResponseDTO;
//import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.CartRestDtoMapper;
//import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class CartControllerTest {
//
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Mock private AddCart addCart;
//    @Mock private GetCart getCart;
//    @Mock private ClearCart clearCart;
//    @Mock private CartRestDtoMapper mapper;
//
//    @InjectMocks private CartController controller;
//
//    private CustomUserDetails userDetails;
//    private static final Integer USER_ID = 1;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//
//        userDetails = mock(CustomUserDetails.class);
//        when(userDetails.getId()).thenReturn(USER_ID);
//
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken(userDetails, null, Collections.emptyList())
//        );
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // POST /api/cart/sync
//    // ─────────────────────────────────────────────────────────────────────────
//
//    @Nested
//    class SyncCartTests {
//
//        @Test
//        void shouldReturn200_withUpdatedCartItems() throws Exception {
//            CartItemRequestDTO item = new CartItemRequestDTO(
//                    CartItem.ItemType.BURGER,      // enum anidado en CartItem
//                    "Burger Clásica",
//                    new BigDecimal("15000.00"),    // price en posición 4
//                    "https://example.com/..."     // imageUrl en posición 5    // int quantity
//            );
//            CartItemResponseDTO responseItem = new CartItemResponseDTO();
//
//            Cart cart = mock(Cart.class);
//
//            when(mapper.toDomainList(any())).thenReturn(Collections.emptyList());
//            when(addCart.handle(eq(USER_ID), any())).thenReturn(cart);
//            when(mapper.toResponseDTOList(cart)).thenReturn(List.of(responseItem));
//
//            mockMvc.perform(post("/api/cart/sync")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .principal(new UsernamePasswordAuthenticationToken(userDetails, null))
//                            .content(objectMapper.writeValueAsString(List.of(item))))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$").isArray());
//
//            verify(addCart, times(1)).handle(eq(USER_ID), any());
//        }
//
//        @Test
//        void shouldReturn200_withEmptyList_whenSyncingEmptyCart() throws Exception {
//            Cart cart = mock(Cart.class);
//
//            when(mapper.toDomainList(any())).thenReturn(Collections.emptyList());
//            when(addCart.handle(eq(USER_ID), any())).thenReturn(cart);
//            when(mapper.toResponseDTOList(cart)).thenReturn(Collections.emptyList());
//
//            mockMvc.perform(post("/api/cart/sync")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .principal(new UsernamePasswordAuthenticationToken(userDetails, null))
//                            .content("[]"))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$").isArray())
//                    .andExpect(jsonPath("$").isEmpty());
//
//            verify(addCart, times(1)).handle(eq(USER_ID), any());
//        }
//
//        @Test
//        void shouldPassUserIdToUseCase_fromCustomUserDetails() throws Exception {
//            CustomUserDetails anotherUser = mock(CustomUserDetails.class);
//            when(anotherUser.getId()).thenReturn(99);
//
//            Cart cart = mock(Cart.class);
//            when(mapper.toDomainList(any())).thenReturn(Collections.emptyList());
//            when(addCart.handle(eq(99), any())).thenReturn(cart);
//            when(mapper.toResponseDTOList(cart)).thenReturn(Collections.emptyList());
//
//            mockMvc.perform(post("/api/cart/sync")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .principal(new UsernamePasswordAuthenticationToken(anotherUser, null))
//                            .content("[]"))
//                    .andExpect(status().isOk());
//
//            verify(addCart, times(1)).handle(eq(99), any());
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // GET /api/cart
//    // ─────────────────────────────────────────────────────────────────────────
//
//    @Nested
//    class GetCartTests {
//
//        @Test
//        void shouldReturn200_withCartItems() throws Exception {
//            Cart cart = mock(Cart.class);
//            CartItemRequestDTO item = new CartItemRequestDTO(
//                    ItemType.PRODUCT,  // typeProduct
//                    1L,                // idProduct
//                    "Burger Clásica",  // name
//                    "https://...",     // imageUrl
//                    15000.0,           // price
//                    2                  // quantity
//            );
//
//            when(getCart.handle(USER_ID)).thenReturn(cart);
//            when(mapper.toResponseDTOList(cart)).thenReturn(List.of(item));
//
//            mockMvc.perform(get("/api/cart")
//                            .principal(new UsernamePasswordAuthenticationToken(userDetails, null)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$").isArray());
//
//            verify(getCart, times(1)).handle(USER_ID);
//        }
//
//        @Test
//        void shouldReturn200_withEmptyList_whenCartIsEmpty() throws Exception {
//            Cart cart = mock(Cart.class);
//
//            when(getCart.handle(USER_ID)).thenReturn(cart);
//            when(mapper.toResponseDTOList(cart)).thenReturn(Collections.emptyList());
//
//            mockMvc.perform(get("/api/cart")
//                            .principal(new UsernamePasswordAuthenticationToken(userDetails, null)))
//                    .andExpect(status().isOk())
//                    .andExpect(jsonPath("$").isEmpty());
//        }
//
//        @Test
//        void shouldCallUseCaseExactlyOnce() throws Exception {
//            Cart cart = mock(Cart.class);
//            when(getCart.handle(USER_ID)).thenReturn(cart);
//            when(mapper.toResponseDTOList(cart)).thenReturn(Collections.emptyList());
//
//            mockMvc.perform(get("/api/cart")
//                            .principal(new UsernamePasswordAuthenticationToken(userDetails, null)))
//                    .andExpect(status().isOk());
//
//            verify(getCart, times(1)).handle(USER_ID);
//            verifyNoMoreInteractions(getCart);
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    // DELETE /api/cart/clear
//    // ─────────────────────────────────────────────────────────────────────────
//
//    @Nested
//    class ClearCartTests {
//
//        @Test
//        void shouldReturn204_whenCartClearedSuccessfully() throws Exception {
//            doNothing().when(clearCart).handle(USER_ID);
//
//            mockMvc.perform(delete("/api/cart/clear")
//                            .principal(new UsernamePasswordAuthenticationToken(userDetails, null)))
//                    .andExpect(status().isNoContent());
//
//            verify(clearCart, times(1)).handle(USER_ID);
//        }
//
//        @Test
//        void shouldPassCorrectUserId_toClearUseCase() throws Exception {
//            CustomUserDetails anotherUser = mock(CustomUserDetails.class);
//            when(anotherUser.getId()).thenReturn(55);
//
//            doNothing().when(clearCart).handle(55);
//
//            mockMvc.perform(delete("/api/cart/clear")
//                            .principal(new UsernamePasswordAuthenticationToken(anotherUser, null)))
//                    .andExpect(status().isNoContent());
//
//            verify(clearCart, times(1)).handle(55);
//            verify(clearCart, never()).handle(USER_ID);
//        }
//
//        @Test
//        void shouldCallUseCaseExactlyOnce() throws Exception {
//            doNothing().when(clearCart).handle(USER_ID);
//
//            mockMvc.perform(delete("/api/cart/clear")
//                            .principal(new UsernamePasswordAuthenticationToken(userDetails, null)))
//                    .andExpect(status().isNoContent());
//
//            verify(clearCart, times(1)).handle(USER_ID);
//            verifyNoMoreInteractions(clearCart);
//        }
//    }
//}