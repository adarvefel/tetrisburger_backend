package com.tetris.tetrisburger_backend.domain.port.in.adittion;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;

public interface ListAddition {
    PageResponse<Addition> handle(Boolean available, PaginationRequest page);

}
