package com.tetris.tetrisburger_backend.domain.port.out;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;

import java.util.List;
import java.util.Optional;

public interface BurgerRepository {

    Burger save(Burger burger);

    Optional<Burger> findById(Integer idBurger);

    // Menú paginado
    PageResponse<Burger> findAllOnMenu(PaginationRequest pagination);


    // Custom del usuario paginado
    PageResponse<Burger> findAllCustomByUserId(Integer idUser, PaginationRequest pagination);

    Optional<Burger> findCustomByIdAndUser(Integer idBurger, Integer idUser);

    Optional<Burger> findMenuById(Integer idBurger);

    // Opcional si quieres búsqueda que excluya borrados lógicos, etc.
    Optional<Burger> findActiveMenuById(Integer idBurger);

    PageResponse<Burger> searchMenuByName(String name, PaginationRequest pagination);
    PageResponse<Burger> searchCustomByName(Integer idUser, String name, PaginationRequest pagination);

}
