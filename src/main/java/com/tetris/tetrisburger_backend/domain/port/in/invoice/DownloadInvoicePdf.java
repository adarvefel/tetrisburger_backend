package com.tetris.tetrisburger_backend.domain.port.in.invoice;

import java.io.InputStream;

public interface DownloadInvoicePdf {
    InputStream handle(Integer idOrder);
}