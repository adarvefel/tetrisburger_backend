package com.tetris.tetrisburger_backend.domain.port.in.adittion;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;

public interface GetAdditionUseCase {
    PageResponse<Addition> getAll(Boolean available, PaginationRequest pagination);
    Addition getById(Integer id);
}
