package com.tetris.tetrisburger_backend.domain.port.in.menucategory;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.MenuCategory;

public interface ListMenuCategory {
    PageResponse<MenuCategory> handle(PaginationRequest request);
}
