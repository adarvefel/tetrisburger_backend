package com.tetris.tetrisburger_backend.domain.port.in.product;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;

public interface ListBurgerIngredients {
    PageResponse<Product> handle(Integer categoryId,PaginationRequest paginationRequest);
}
