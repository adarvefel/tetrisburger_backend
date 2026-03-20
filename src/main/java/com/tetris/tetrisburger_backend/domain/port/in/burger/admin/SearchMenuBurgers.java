package com.tetris.tetrisburger_backend.domain.port.in.burger.admin;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;

public interface SearchMenuBurgers {
    PageResponse<Burger> search(SearchMenuBurgersQuery query, PaginationRequest pagination);
}
