package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.GetInvoiceByOrder;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.InvoiceRestDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    private MockMvc mockMvc;

    @Mock private GetInvoiceByOrder getInvoiceByOrder;
    @Mock private InvoiceRestDtoMapper mapper;

    @InjectMocks private InvoiceController controller;

    private static final Integer ORDER_ID = 42;
    private static final String PDF_URL = "https://s3.amazonaws.com/bucket/invoice-42.pdf";

    @BeforeEach
    void setUp() {
        // standaloneSetup: NO levanta contexto Spring completo.
        // @PreAuthorize no se ejecuta aquí — la seguridad se testea en tests de integración.
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/invoices/order/{idOrder}/pdf
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    class GetPdfTests {

        @Test
        void shouldReturn200_withPdfUrl_whenInvoiceExists() throws Exception {
            Invoice invoice = mock(Invoice.class);
            when(invoice.getPdfUrl()).thenReturn(PDF_URL);
            when(getInvoiceByOrder.handle(ORDER_ID)).thenReturn(invoice);

            mockMvc.perform(get("/api/invoices/order/{idOrder}/pdf", ORDER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().string(PDF_URL));

            verify(getInvoiceByOrder, times(1)).handle(ORDER_ID);
            verify(invoice, times(1)).getPdfUrl();
        }

        @Test
        void shouldPassCorrectOrderId_toUseCase() throws Exception {
            Integer differentOrderId = 99;
            Invoice invoice = mock(Invoice.class);
            when(invoice.getPdfUrl()).thenReturn("https://s3.amazonaws.com/bucket/invoice-99.pdf");
            when(getInvoiceByOrder.handle(differentOrderId)).thenReturn(invoice);

            mockMvc.perform(get("/api/invoices/order/{idOrder}/pdf", differentOrderId))
                    .andExpect(status().isOk());

            verify(getInvoiceByOrder).handle(differentOrderId);
            verify(getInvoiceByOrder, never()).handle(ORDER_ID);
        }

        @Test
        void shouldCallUseCaseExactlyOnce() throws Exception {
            Invoice invoice = mock(Invoice.class);
            when(invoice.getPdfUrl()).thenReturn(PDF_URL);
            when(getInvoiceByOrder.handle(ORDER_ID)).thenReturn(invoice);

            mockMvc.perform(get("/api/invoices/order/{idOrder}/pdf", ORDER_ID))
                    .andExpect(status().isOk());

            verify(getInvoiceByOrder, times(1)).handle(ORDER_ID);
            verifyNoMoreInteractions(getInvoiceByOrder);
        }

        @Test
        void shouldReturnPdfUrl_asPlainString() throws Exception {
            Invoice invoice = mock(Invoice.class);
            when(invoice.getPdfUrl()).thenReturn(PDF_URL);
            when(getInvoiceByOrder.handle(ORDER_ID)).thenReturn(invoice);

            mockMvc.perform(get("/api/invoices/order/{idOrder}/pdf", ORDER_ID))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/plain"))
                    .andExpect(content().string(PDF_URL));
        }
    }
}