package com.tetris.tetrisburger_backend.domain.port.in.menu;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Menu;

public interface ListMenu {
    PageResponse<Menu> handle(PaginationRequest request);
}