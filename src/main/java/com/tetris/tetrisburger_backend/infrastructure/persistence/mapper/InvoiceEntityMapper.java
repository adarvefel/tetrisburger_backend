package com.tetris.tetrisburger_backend.infrastructure.persistence.mapper;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.InvoiceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceEntityMapper {

    default Invoice toDomain(InvoiceEntity e) {
        if (e == null) return null;
        return Invoice.reconstitute(
                e.getIdInvoice(), e.getIdOrder(), e.getIdPayment(),
                e.getInvoiceNumber(), e.getExternalInvoiceId(),
                e.getTotalAmount(), e.getStatus(), e.getInvoiceDate(),
                e.getPdfUrl()
        );
    }

    default InvoiceEntity toEntity(Invoice i) {
        if (i == null) return null;
        InvoiceEntity e = new InvoiceEntity();
        e.setIdInvoice(i.getIdInvoice());
        e.setIdOrder(i.getIdOrder());
        e.setIdPayment(i.getIdPayment());
        e.setInvoiceNumber(i.getInvoiceNumber());
        e.setExternalInvoiceId(i.getExternalInvoiceId());
        e.setTotalAmount(i.getTotalAmount());
        e.setStatus(i.getStatus());
        e.setInvoiceDate(i.getInvoiceDate());
        e.setPdfUrl(i.getPdfUrl());
        return e;
    }
}