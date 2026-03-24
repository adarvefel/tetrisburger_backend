package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.Invoice;
import com.tetris.tetrisburger_backend.domain.port.out.InvoiceRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.InvoiceEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.InvoiceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InvoiceAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository jpa;
    private final InvoiceEntityMapper mapper;

    public InvoiceAdapter(InvoiceJpaRepository jpa, InvoiceEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return mapper.toDomain(jpa.save(mapper.toEntity(invoice)));
    }

    @Override
    public Optional<Invoice> findById(Integer idInvoice) {
        return jpa.findById(idInvoice).map(mapper::toDomain);
    }

    @Override
    public Optional<Invoice> findByOrderId(Integer idOrder) {
        return jpa.findByIdOrder(idOrder).map(mapper::toDomain);
    }
}