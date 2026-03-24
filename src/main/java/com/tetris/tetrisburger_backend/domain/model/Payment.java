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
    private BigDecimal amountReceived;
    private BigDecimal changeAmount;
    private LocalDateTime paidAt;

    private Payment() {}

    public static Payment create(
            Integer idOrder,
            Integer idUser,
            PaymentMethod paymentMethod,
            BigDecimal amount,
            BigDecimal amountReceived
    ) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        if (amountReceived == null || amountReceived.compareTo(amount) < 0)
            throw new IllegalArgumentException("El monto recibido no puede ser menor al total");

        Payment p = new Payment();
        p.idOrder = idOrder;
        p.idUser = idUser;
        p.paymentMethod = paymentMethod;
        p.amount = amount;
        p.amountReceived = amountReceived;
        p.changeAmount = amountReceived.subtract(amount);
        p.paidAt = LocalDateTime.now();
        return p;
    }

    public static Payment reconstitute(
            Integer idPayment, Integer idOrder, Integer idUser,
            PaymentMethod paymentMethod, BigDecimal amount,
            BigDecimal amountReceived, BigDecimal changeAmount,
            LocalDateTime paidAt
    ) {
        Payment p = new Payment();
        p.idPayment = idPayment;
        p.idOrder = idOrder;
        p.idUser = idUser;
        p.paymentMethod = paymentMethod;
        p.amount = amount;
        p.amountReceived = amountReceived;
        p.changeAmount = changeAmount;
        p.paidAt = paidAt;
        return p;
    }

    public Integer getIdPayment()           { return idPayment; }
    public Integer getIdOrder()             { return idOrder; }
    public Integer getIdUser()              { return idUser; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public BigDecimal getAmount()           { return amount; }
    public BigDecimal getAmountReceived()   { return amountReceived; }
    public BigDecimal getChangeAmount()     { return changeAmount; }
    public LocalDateTime getPaidAt()        { return paidAt; }
    public void setIdPayment(Integer id)    { this.idPayment = id; }
}