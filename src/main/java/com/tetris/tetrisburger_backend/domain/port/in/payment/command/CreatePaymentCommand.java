package com.tetris.tetrisburger_backend.domain.port.in.payment.command;

import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;


import java.math.BigDecimal;

public record CreatePaymentCommand(

        Integer idOrder,
        Integer idUser,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        BigDecimal amountReceived
) {}
