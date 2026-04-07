package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private Integer idPayment;
    private Integer idOrder;
    private Integer idUser;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paidAt;

    private Payment() {}

    public static Payment create(
            Integer idOrder,
            Integer idUser,
            PaymentMethod paymentMethod,
            BigDecimal amount
    ) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");

        Payment p = new Payment();
        p.idOrder = idOrder;
        p.idUser = idUser;
        p.paymentMethod = paymentMethod;
        p.amount = amount;
        p.paidAt = LocalDateTime.now();
        return p;
    }

    public static Payment reconstitute(
            Integer idPayment, Integer idOrder, Integer idUser,
            PaymentMethod paymentMethod, BigDecimal amount,
            LocalDateTime paidAt
    ) {
        Payment p = new Payment();
        p.idPayment = idPayment;
        p.idOrder = idOrder;
        p.idUser = idUser;
        p.paymentMethod = paymentMethod;
        p.amount = amount;
        p.paidAt = paidAt;
        return p;
    }

    public Integer getIdPayment()           { return idPayment; }
    public Integer getIdOrder()             { return idOrder; }
    public Integer getIdUser()              { return idUser; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public BigDecimal getAmount()           { return amount; }
    public LocalDateTime getPaidAt()        { return paidAt; }
    public void setIdPayment(Integer id)    { this.idPayment = id; }
}