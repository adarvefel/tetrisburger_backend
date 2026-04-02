
package com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment;

import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;
import java.math.BigDecimal;

public record CreatePaymentRequestDTO(
        Integer idOrder,
        PaymentMethod paymentMethod
) {}
