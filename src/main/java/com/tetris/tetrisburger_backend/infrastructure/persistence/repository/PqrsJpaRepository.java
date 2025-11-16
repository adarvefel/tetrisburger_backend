package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PqrsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PqrsJpaRepository extends JpaRepository<PqrsEntity, Integer> {

    Page<PqrsEntity> findAllByDeletedAtIsNull(Pageable pageable);
}
