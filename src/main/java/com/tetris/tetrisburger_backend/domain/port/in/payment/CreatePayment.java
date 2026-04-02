package com.tetris.tetrisburger_backend.domain.port.in.payment;

import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.in.payment.command.CreatePaymentCommand;

public interface CreatePayment {
    Payment handle(CreatePaymentCommand command);
}
