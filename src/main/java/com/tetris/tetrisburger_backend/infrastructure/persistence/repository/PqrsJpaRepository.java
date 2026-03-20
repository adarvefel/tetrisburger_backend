package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.PqrsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PqrsJpaRepository extends JpaRepository<PqrsEntity, Integer> {

    Page<PqrsEntity> findAllByDeletedAtIsNull(Pageable pageable);

    Page<PqrsEntity> findAllByIdUserAndDeletedAtIsNull(Integer idUser, Pageable pageable);

    Page<PqrsEntity> findAllByTypeAndDeletedAtIsNull(String type, Pageable pageable);

    Page<PqrsEntity> findAllByStatusAndDeletedAtIsNull(String status, Pageable pageable);

    Page<PqrsEntity> findAllByPriorityAndDeletedAtIsNull(String priority, Pageable pageable);
}
