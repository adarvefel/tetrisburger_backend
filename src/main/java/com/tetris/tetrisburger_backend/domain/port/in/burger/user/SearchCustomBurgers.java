package com.tetris.tetrisburger_backend.domain.port.in.burger.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchCustomBurgersQuery;

public interface SearchCustomBurgers {
    PageResponse<Burger> search(SearchCustomBurgersQuery query, PaginationRequest pagination);

}