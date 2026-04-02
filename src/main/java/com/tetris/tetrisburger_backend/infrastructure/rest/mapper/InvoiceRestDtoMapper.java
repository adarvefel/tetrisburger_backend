package com.tetris.tetrisburger_backend.infrastructure.rest.mapper;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.invoice.InvoiceResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceRestDtoMapper {

    default InvoiceResponseDTO toResponseDTO(Invoice i) {
        if (i == null) return null;
        return new InvoiceResponseDTO(
                i.getIdInvoice(),
                i.getIdOrder(),
                i.getIdPayment(),
                i.getInvoiceNumber(),
                i.getExternalInvoiceId(),
                i.getTotalAmount(),
                i.getStatus().name(),
                i.getInvoiceDate(),
                i.getPdfUrl()
        );
    }
}
