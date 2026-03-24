package com.tetris.tetrisburger_backend.application.usecase.invoice;

import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Invoice;


import com.tetris.tetrisburger_backend.domain.port.in.invoice.GetInvoiceByOrder;
import com.tetris.tetrisburger_backend.domain.port.out.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetInvoiceByOrderUseCase implements GetInvoiceByOrder {

    private final InvoiceRepository invoiceRepository;

    public GetInvoiceByOrderUseCase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }


    @Override
    public Invoice handle(Integer idOrder) {
        return invoiceRepository.findByOrderId(idOrder)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Factura no encontrada para orden: " + idOrder));

    }
}
