package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.DownloadInvoicePdf;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.GetInvoiceByOrder;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.invoice.InvoiceResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.InvoiceRestDtoMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final GetInvoiceByOrder getInvoiceByOrder;
    private final DownloadInvoicePdf downloadInvoicePdf;
    private final InvoiceRestDtoMapper mapper;

    public InvoiceController(GetInvoiceByOrder getInvoiceByOrder, DownloadInvoicePdf downloadInvoicePdf, InvoiceRestDtoMapper mapper) {
        this.getInvoiceByOrder = getInvoiceByOrder;
        this.downloadInvoicePdf = downloadInvoicePdf;
        this.mapper = mapper;
    }

    @GetMapping("/order/{idOrder}/pdf")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<String> getPdf(@PathVariable Integer idOrder) {
        Invoice invoice = getInvoiceByOrder.handle(idOrder);
        return ResponseEntity.ok(invoice.getPdfUrl());
    }

    @GetMapping("/order/{idOrder}/pdf/download")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable Integer idOrder) {
        InputStream pdfStream = downloadInvoicePdf.handle(idOrder);
        String filename = "factura-orden-" + idOrder + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }
}

