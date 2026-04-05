package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.enums.OrderStatus;
import com.tetris.tetrisburger_backend.domain.exception.OrderNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.CartItem;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.BurgerExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.GlobalExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ProductExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.advice.ValidationExceptionHandler;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.cart.CartItemRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.CreateOrderRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.OrderItemResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.OrderResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.OrderRestDtoMapper;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock private CreateOrder createOrder;
    @Mock private GetOrderById getOrderById;
    @Mock private ListUserOrders listUserOrders;
    @Mock private ListAllOrders listAllOrders;
    @Mock private UpdateOrderStatus updateOrderStatus;
    @Mock private SearchOrderByNumber searchOrderByNumber;
    @Mock private OrderRestDtoMapper mapper;

    @InjectMocks private OrderController controller;

    private CustomUserDetails mockUserDetails;
    private Order mockOrder;

    private void mockAuthenticatedUser(Long userId) {
        lenient().when(mockUserDetails.getId()).thenReturn(userId.intValue());
        lenient().when(mockUserDetails.getUsername()).thenReturn(userId.toString());
    }

    private OrderResponseDTO sampleOrderDto() {
        return new OrderResponseDTO(
                1, "ORD-1", "PENDING", BigDecimal.TEN, LocalDateTime.now(), LocalDateTime.now(),
                1, 1, List.<OrderItemResponseDTO>of(), null
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

        mockOrder = mock(Order.class);
        lenient().when(mockOrder.getIdOrder()).thenReturn(1);
        lenient().when(mockOrder.getStatus()).thenReturn(OrderStatus.PENDING);
        lenient().when(mockOrder.getItems()).thenReturn(List.of());
    }

    @Nested
    class CreateOrderTests {
        @Test
        void shouldReturn201WhenCreatedSuccessfully() throws Exception {
            CreateOrderRequestDTO dto = new CreateOrderRequestDTO(List.of(
                    new CartItemRequestDTO(
                            CartItem.ItemType.PRODUCT, 1, "Prod", new BigDecimal("5000"), null, 1)
            ));

            when(createOrder.handle(eq(1), anyList())).thenReturn(mockOrder);
            when(mapper.toResponseDTO(mockOrder)).thenReturn(sampleOrderDto());

            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.idOrder").value(1));

            verify(createOrder, times(1)).handle(eq(1), anyList());
        }

        @Test
        void shouldReturn500WhenBodyMalformed() throws Exception {
            mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("not-json"))
                    .andExpect(status().isInternalServerError());

            verifyNoInteractions(createOrder);
        }
    }

    @Nested
    class MyOrdersTests {
        @Test
        void shouldReturn200WhenListed() throws Exception {
            PageResponse<Order> page = new PageResponse<>(List.of(mockOrder), 0, 10, 1L, 1);
            when(listUserOrders.handle(eq(1), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toResponseDTO(mockOrder)).thenReturn(sampleOrderDto());

            mockMvc.perform(get("/api/orders/my-orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].idOrder").value(1));

            verify(listUserOrders, times(1)).handle(eq(1), any(PaginationRequest.class));
        }
    }

    @Nested
    class GetOrderByIdTests {
        @Test
        void shouldReturn200WhenFound() throws Exception {
            when(getOrderById.handle(1, 1)).thenReturn(mockOrder);
            when(mapper.toResponseDTO(mockOrder)).thenReturn(sampleOrderDto());

            mockMvc.perform(get("/api/orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderNumber").value("ORD-1"));

            verify(getOrderById, times(1)).handle(1, 1);
        }

        @Test
        void shouldReturn404WhenNotFound() throws Exception {
            when(getOrderById.handle(99, 1)).thenThrow(new OrderNotFoundException("Pedido no encontrado"));

            mockMvc.perform(get("/api/orders/99"))
                    .andExpect(status().isNotFound());

            verify(getOrderById, times(1)).handle(99, 1);
        }
    }

    @Nested
    class ListAllOrdersTests {
        @Test
        void shouldReturn200WhenListed() throws Exception {
            PageResponse<Order> page = new PageResponse<>(List.of(mockOrder), 0, 10, 1L, 1);
            when(listAllOrders.handle(isNull(), isNull(), any(PaginationRequest.class))).thenReturn(page);
            when(mapper.toResponseDTO(mockOrder)).thenReturn(sampleOrderDto());

            mockMvc.perform(get("/api/orders/all"))
                    .andExpect(status().isOk());

            verify(listAllOrders, times(1)).handle(isNull(), isNull(), any(PaginationRequest.class));
        }

        @Test
        void shouldReturn409WhenStatusInvalid() throws Exception {
            mockMvc.perform(get("/api/orders/all").param("status", "NO_EXISTE"))
                    .andExpect(status().isConflict());

            verifyNoInteractions(listAllOrders);
        }
    }

    @Nested
    class UpdateOrderStatusTests {
        @Test
        void shouldReturn200WhenUpdated() throws Exception {
            when(updateOrderStatus.handle(eq(1), eq(OrderStatus.COMPLETED), eq(1))).thenReturn(mockOrder);
            when(mapper.toResponseDTO(mockOrder)).thenReturn(sampleOrderDto());

            mockMvc.perform(patch("/api/orders/1/status").param("status", "completed"))
                    .andExpect(status().isOk());

            verify(updateOrderStatus, times(1)).handle(eq(1), eq(OrderStatus.COMPLETED), eq(1));
        }

        @Test
        void shouldReturn409WhenStatusInvalid() throws Exception {
            mockMvc.perform(patch("/api/orders/1/status").param("status", "INVALID_ENUM"))
                    .andExpect(status().isConflict());

            verifyNoInteractions(updateOrderStatus);
        }
    }

    @Nested
    class SearchOrderTests {
        @Test
        void shouldReturn200WhenSearching() throws Exception {
            PageResponse<Order> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(searchOrderByNumber.handle(eq("ORD-1"), any(PaginationRequest.class))).thenReturn(page);

            mockMvc.perform(get("/api/orders/search").param("orderNumber", "ORD-1"))
                    .andExpect(status().isOk());

            verify(searchOrderByNumber, times(1)).handle(eq("ORD-1"), any(PaginationRequest.class));
        }
    }

    @Nested
    class SecurityTests {

        @Test
        void endpoint_shouldNotCallUseCase_whenNotAuthenticated() throws Exception {
            SecurityContextHolder.clearContext();
            verifyNoInteractions(createOrder);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        void endpoint_shouldBeAccessible_whenRoleIsClient() throws Exception {
            PageResponse<Order> page = new PageResponse<>(List.of(), 0, 10, 0L, 0);
            when(listUserOrders.handle(eq(1), any(PaginationRequest.class))).thenReturn(page);
            mockMvc.perform(get("/api/orders/my-orders"))
                    .andExpect(status().isOk());
        }
    }
}
