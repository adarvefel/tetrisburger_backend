package com.tetris.tetrisburger_backend.application.usecase.payment;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.DownloadInvoicePdf;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.GetInvoiceByOrder;
import com.tetris.tetrisburger_backend.domain.port.out.PdfStoragePort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Transactional
public class DownloadInvoicePdfUseCase implements DownloadInvoicePdf {

    private final GetInvoiceByOrder getInvoiceByOrder;
    private final PdfStoragePort pdfStoragePort;

    public DownloadInvoicePdfUseCase(GetInvoiceByOrder getInvoiceByOrder,
                                     PdfStoragePort pdfStoragePort) {
        this.getInvoiceByOrder = getInvoiceByOrder;
        this.pdfStoragePort = pdfStoragePort;
    }

    @Override
    public InputStream handle(Integer idOrder) {
        Invoice invoice = getInvoiceByOrder.handle(idOrder);
        return pdfStoragePort.downloadPdf(invoice.getPdfUrl());
    }
}