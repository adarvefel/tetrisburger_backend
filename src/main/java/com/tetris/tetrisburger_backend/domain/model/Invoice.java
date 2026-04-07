package com.tetris.tetrisburger_backend.domain.model;

import com.tetris.tetrisburger_backend.domain.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Invoice {

    private Integer idInvoice;
    private Integer idOrder;
    private Integer idPayment;
    private String invoiceNumber;
    private String externalInvoiceId;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private LocalDateTime invoiceDate;
    private String pdfUrl;

    private Invoice() {}

    public static Invoice create(
            Integer idOrder,
            Integer idPayment,
            BigDecimal totalAmount
    ) {
        Invoice i = new Invoice();
        i.idOrder = idOrder;
        i.idPayment = idPayment;
        i.totalAmount = totalAmount;
        i.status = InvoiceStatus.PENDING;
        i.invoiceDate = LocalDateTime.now();
        return i;
    }


    public static Invoice reconstitute(
            Integer idInvoice, Integer idOrder, Integer idPayment,
            String invoiceNumber, String externalInvoiceId,
            BigDecimal totalAmount, InvoiceStatus status,
            LocalDateTime invoiceDate, String pdfUrl
    ) {
        Invoice i = new Invoice();
        i.idInvoice = idInvoice;
        i.idOrder = idOrder;
        i.idPayment = idPayment;
        i.invoiceNumber = invoiceNumber;
        i.externalInvoiceId = externalInvoiceId;
        i.totalAmount = totalAmount;
        i.status = status;
        i.invoiceDate = invoiceDate;
        i.pdfUrl = pdfUrl;
        return i;
    }

    public void markAsIssued(String externalInvoiceId, String invoiceNumber, String pdfUrl) {
        this.externalInvoiceId = externalInvoiceId;
        this.invoiceNumber = invoiceNumber;
        this.pdfUrl = pdfUrl;
        this.status = InvoiceStatus.ISSUED;
    }

    public void markAsFailed() {
        this.status = InvoiceStatus.FAILED;
        this.invoiceNumber = "ERR-" + this.idOrder; // ✅

    }
    public String getPdfUrl() { return pdfUrl; }
    public Integer getIdInvoice()           { return idInvoice; }
    public Integer getIdOrder()             { return idOrder; }
    public Integer getIdPayment()           { return idPayment; }
    public String getInvoiceNumber()        { return invoiceNumber; }
    public String getExternalInvoiceId()    { return externalInvoiceId; }
    public BigDecimal getTotalAmount()      { return totalAmount; }
    public InvoiceStatus getStatus()        { return status; }
    public LocalDateTime getInvoiceDate()   { return invoiceDate; }
    public void setIdInvoice(Integer id)    { this.idInvoice = id; }
}