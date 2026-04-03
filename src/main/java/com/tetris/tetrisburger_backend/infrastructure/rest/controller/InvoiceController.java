package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.port.in.invoice.GetInvoiceByOrder;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.InvoiceRestDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final GetInvoiceByOrder getInvoiceByOrder;
    private final InvoiceRestDtoMapper mapper;

    public InvoiceController(GetInvoiceByOrder getInvoiceByOrder, InvoiceRestDtoMapper mapper) {
        this.getInvoiceByOrder = getInvoiceByOrder;
        this.mapper = mapper;
    }

    @GetMapping("/order/{idOrder}/pdf")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<String> getPdf(@PathVariable Integer idOrder) {
        Invoice invoice = getInvoiceByOrder.handle(idOrder);
        return ResponseEntity.ok(invoice.getPdfUrl());
    }


}

