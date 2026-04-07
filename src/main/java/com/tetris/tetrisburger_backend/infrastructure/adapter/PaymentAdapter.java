package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.model.Payment;
import com.tetris.tetrisburger_backend.domain.port.out.PaymentRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.mapper.PaymentEntityMapper;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.PaymentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PaymentAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpa;
    private final PaymentEntityMapper mapper;

    public PaymentAdapter(PaymentJpaRepository jpa, PaymentEntityMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Payment save(Payment payment) {
        return mapper.toDomain(jpa.save(mapper.toEntity(payment)));
    }

    @Override
    public Optional<Payment> findById(Integer id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Payment> findByOrderId(Integer idOrder) {
        return jpa.findByIdOrder(idOrder).map(mapper::toDomain); // ← conecta al JPA
    }
}
