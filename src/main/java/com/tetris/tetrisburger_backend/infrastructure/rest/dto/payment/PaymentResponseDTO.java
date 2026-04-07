package com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDTO(
        Integer idPayment,
        Integer idOrder,
        Integer idUser,
        String paymentMethod,
        BigDecimal amount,
        LocalDateTime paidAt
) {}
