package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.payment.PaymentResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentRestDtoMapper {

    default PaymentResponseDTO toResponseDTO(Payment p) {
        if (p == null) return null;
        return new PaymentResponseDTO(
                p.getIdPayment(),
                p.getIdOrder(),
                p.getIdUser(),
                p.getPaymentMethod().name(),
                p.getAmount(),
                p.getAmountReceived(),
                p.getChangeAmount(),
                p.getPaidAt()
        );
    }
}
