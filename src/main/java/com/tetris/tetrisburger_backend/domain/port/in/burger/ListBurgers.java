package com.tetris.tetrisburger_backend.domain.port.in.burger;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;


public interface ListBurgers {
    PageResponse<Burger> handle(PaginationRequest paginationRequest );
}
