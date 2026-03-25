package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, Integer> {
    Optional<InvoiceEntity> findByIdOrder(Integer idOrder);
}