// src/main/java/com/tetris/tetrisburger_backend/domain/port/out/AdditionRepository.java
package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;

import java.util.Optional;

public interface AdditionRepository {
    PageResponse<Addition> findAll(Boolean available, PaginationRequest pagination);
    Optional<Addition> findById(Integer id);
    boolean existsByNameIgnoreCase(String name);
    Addition save(Addition addition);
    PageResponse<Addition> findByName(String name, PaginationRequest pagination);

}
