package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentJpaRepository
        extends JpaRepository<PaymentEntity, Integer> {
        Optional<PaymentEntity> findByIdOrder(Integer idOrder);
}