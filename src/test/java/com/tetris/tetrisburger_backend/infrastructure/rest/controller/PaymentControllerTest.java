package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;
import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.CreatePayment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.command.CreatePaymentCommand;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment.CreatePaymentRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment.PaymentResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.PaymentRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock private CreatePayment createPayment;
    @Mock private PaymentRestDtoMapper mapper;
    @Mock private CustomUserDetails mockUserDetails;

    @InjectMocks
    private PaymentController controller;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Integer USER_ID   = 42;
    private static final Integer ORDER_ID  = 10;
    private static final String  METHOD    = "CASH";

    // JSON válido reutilizable en todos los tests
    private static final String VALID_BODY =
            """
            {"idOrder": 10, "paymentMethod": "CASH"}
            """;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        // Fix 1
        lenient().when(mockUserDetails.getId()).thenReturn(USER_ID);

        // Fix 2
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

    // ─── POST /api/payments ──────────────────────────────────────────────────

    @Test
    void create_returnsCreated_withMappedPayment() throws Exception {
        Payment domain     = mock(Payment.class);
        PaymentResponseDTO responseDTO = mock(PaymentResponseDTO.class);

        when(createPayment.handle(any(CreatePaymentCommand.class))).thenReturn(domain);
        when(mapper.toResponseDTO(domain)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());

        verify(createPayment).handle(any(CreatePaymentCommand.class));
        verify(mapper).toResponseDTO(domain);
    }

    @Test
    void create_buildsCommandWithCorrectFields() throws Exception {
        Payment domain = mock(Payment.class);
        ArgumentCaptor<CreatePaymentCommand> captor =
                ArgumentCaptor.forClass(CreatePaymentCommand.class);

        when(createPayment.handle(captor.capture())).thenReturn(domain);
        when(mapper.toResponseDTO(domain)).thenReturn(mock(PaymentResponseDTO.class));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());

        CreatePaymentCommand cmd = captor.getValue();
        // Ajusta los getters según tu record/class: idOrder(), employeeId(), paymentMethod()
        assertEquals(ORDER_ID, cmd.idOrder());
        assertEquals(USER_ID,  cmd.idUser());   // segundo param = user.getId()
        assertEquals(PaymentMethod.CASH,   cmd.paymentMethod());
    }

    @Test
    void create_withoutContentType_returnsUnsupportedMediaType() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .content(VALID_BODY))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void create_whenServiceThrows_propagatesException() {
        when(createPayment.handle(any(CreatePaymentCommand.class)))
                .thenThrow(new RuntimeException("Payment processing failed"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY)));
    }
}