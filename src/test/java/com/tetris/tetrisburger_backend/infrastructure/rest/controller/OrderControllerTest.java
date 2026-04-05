package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Order;
import com.tetris.tetrisburger_backend.domain.port.in.order.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.order.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        mockOrder = mock(Order.class);
    }

    @Nested
    class CreateOrderTests {
        @Test
        void shouldCreateOrderAndReturn201() throws Exception {
            when(createOrder.handle(eq(1), any())).thenReturn(mockOrder);

            mockMvc.perform(post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"items\": []}"))
                    .andExpect(status().isCreated());

            verify(createOrder).handle(eq(1), any());
        }
    }

    @Nested
    class MyOrdersTests {
        @Test
        void shouldListMyOrdersAndReturn200() throws Exception {
            PageResponse<Order> pageResponse = new PageResponse<>(List.of(mockOrder), 0, 10, 1L, 1);

            when(listUserOrders.handle(eq(1), any(PaginationRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/orders/my-orders"))
                    .andExpect(status().isOk());

            verify(listUserOrders).handle(eq(1), any());
        }
    }

    @Nested
    class GetByIdTests {
        @Test
        void shouldGetOrderByIdAndReturn200() throws Exception {
            when(getOrderById.handle(1, 1)).thenReturn(mockOrder);

            mockMvc.perform(get("/api/orders/1"))
                    .andExpect(status().isOk());

            verify(getOrderById).handle(1, 1);
        }
    }

    @Nested
    class ListAllOrdersTests {
        @Test
        void shouldListAllOrdersAndReturn200() throws Exception {
            PageResponse<Order> pageResponse = new PageResponse<>(List.of(mockOrder), 0, 10, 1L, 1);

            when(listAllOrders.handle(any(), any(), any(PaginationRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/orders/all"))
                    .andExpect(status().isOk());

            verify(listAllOrders).handle(any(), any(), any());
        }
    }

    @Nested
    class SearchOrderTests {
        @Test
        void shouldSearchOrderByNumberAndReturn200() throws Exception {
            PageResponse<Order> pageResponse = new PageResponse<>(List.of(mockOrder), 0, 10, 1L, 1);

            when(searchOrderByNumber.handle(eq("ORD-123"), any(PaginationRequest.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/orders/search").param("orderNumber", "ORD-123"))
                    .andExpect(status().isOk());

            verify(searchOrderByNumber).handle(eq("ORD-123"), any());
        }
    }

    @Nested
    class UpdateStatusTests {
        @Test
        void shouldUpdateOrderStatusAndReturn200() throws Exception {
            when(updateOrderStatus.handle(eq(1), any(), eq(1))).thenReturn(mockOrder);

            mockMvc.perform(patch("/api/orders/1/status").param("status", "COMPLETED"))
                    .andExpect(status().isOk());

            verify(updateOrderStatus).handle(eq(1), any(), eq(1));
        }
    }
}
