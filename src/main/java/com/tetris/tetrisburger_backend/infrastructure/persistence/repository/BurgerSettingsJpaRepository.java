package com.tetris.tetrisburger_backend.infrastructure.persistence.repository;

import com.tetris.tetrisburger_backend.infrastructure.persistence.entity.BurgerSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BurgerSettingsJpaRepository extends JpaRepository<BurgerSettingsEntity,Integer> {
}
