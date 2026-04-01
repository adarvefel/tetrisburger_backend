package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import com.tetris.tetrisburger_backend.domain.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_payment")
    private Integer idPayment;

    @Column(name = "id_order")
    private Integer idOrder;

    @Column(name = "id_user")
    private Integer idUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}