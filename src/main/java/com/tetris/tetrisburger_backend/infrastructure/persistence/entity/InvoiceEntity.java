package com.tetris.tetrisburger_backend.infrastructure.persistence.entity;

import com.tetris.tetrisburger_backend.domain.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invoice")
    private Integer idInvoice;

    @Column(name = "id_order")
    private Integer idOrder;

    @Column(name = "id_payment")
    private Integer idPayment;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "external_invoice_id", length = 100)
    private String externalInvoiceId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;
}