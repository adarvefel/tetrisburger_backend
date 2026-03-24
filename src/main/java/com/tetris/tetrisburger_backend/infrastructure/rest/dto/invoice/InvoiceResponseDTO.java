package com.tetris.tetrisburger_backend.infrastructure.rest.dto.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvoiceResponseDTO(
        Integer idInvoice,
        Integer idOrder,
        Integer idPayment,
        String invoiceNumber,
        String externalInvoiceId,
        BigDecimal totalAmount,
        String status,
        LocalDateTime invoiceDate
) {}
