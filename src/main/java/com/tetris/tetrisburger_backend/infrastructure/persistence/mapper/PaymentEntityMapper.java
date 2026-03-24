package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentEntityMapper {

    default Payment toDomain(PaymentEntity e) {
        if (e == null) return null;
        return Payment.reconstitute(
                e.getIdPayment(), e.getIdOrder(), e.getIdUser(),
                e.getPaymentMethod(), e.getAmount(),
                e.getAmountReceived(), e.getChangeAmount(), e.getPaidAt()
        );
    }

    default PaymentEntity toEntity(Payment p) {
        if (p == null) return null;
        PaymentEntity e = new PaymentEntity();
        e.setIdPayment(p.getIdPayment());
        e.setIdOrder(p.getIdOrder());
        e.setIdUser(p.getIdUser());
        e.setPaymentMethod(p.getPaymentMethod());
        e.setAmount(p.getAmount());
        e.setAmountReceived(p.getAmountReceived());
        e.setChangeAmount(p.getChangeAmount());
        e.setPaidAt(p.getPaidAt());
        return e;
    }
}