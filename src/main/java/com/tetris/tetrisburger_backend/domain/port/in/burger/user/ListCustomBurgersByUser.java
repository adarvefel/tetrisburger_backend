// src/main/java/com/tetris/tetrisburger_backend/domain/port/in/burger/ListCustomBurgersByUser.java
package com.tetris.tetrisburger_backend.domain.port.in.burger.user;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;

public interface ListCustomBurgersByUser {

    PageResponse<Burger> handle(Integer idUser, PaginationRequest pagination);
}
