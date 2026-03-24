package com.tetris.tetrisburger_backend.domain.port.in.invoice;

import com.tetris.tetrisburger_backend.domain.model.Invoice;

public interface GetInvoiceByOrder {
    Invoice handle(Integer idOrder);
}
