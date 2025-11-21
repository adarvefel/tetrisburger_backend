package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.application.usecase.auth.LoginWithGoogleUseCase;
import com.tetris.tetrisburger_backend.application.usecase.burger.UpdateMenuBurgerPriceUseCase;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/burgers")
public class AdminBurgerController {

    private final CreateMenuBurger createMenuBurger;
    private final GetBurgerById getBurgerById;
    private final ListBurgers listBurgers;
    private final UpdateMenuBurger updateMenuBurger;
    private final DeleteMenuBurger deleteMenuBurger;
    private final UpdateMenuBurgerPrice updateMenuBurgerPrice;
    private final SearchMenuBurgers searchMenuBurgers;
    private final BurgerRestDtoMapper mapper;

    public AdminBurgerController(CreateMenuBurger createMenuBurger, GetBurgerById getBurgerById, ListBurgers listBurgers, UpdateMenuBurger updateMenuBurger, DeleteMenuBurger deleteMenuBurger, UpdateMenuBurgerPrice updateMenuBurgerPrice, SearchMenuBurgers searchMenuBurgers, BurgerRestDtoMapper mapper) {
        this.createMenuBurger = createMenuBurger;
        this.getBurgerById = getBurgerById;
        this.listBurgers = listBurgers;
        this.updateMenuBurger = updateMenuBurger;
        this.deleteMenuBurger = deleteMenuBurger;
        this.updateMenuBurgerPrice = updateMenuBurgerPrice;
        this.searchMenuBurgers = searchMenuBurgers;
        this.mapper = mapper;
    }

    // ========= CREAR BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @PostMapping("/menu")
    public ResponseEntity<MenuBurgerResponseDTO> createMenuBurger(
            @RequestBody CreateMenuBurgerRequestDTO request
    ) {
        var command = mapper.toMenuCommand(request);
        Burger burger = createMenuBurger.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toMenuResponse(burger));
    }

    // ========= ACTUALIZAR BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @PutMapping("/menu/{idBurger}")
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurger(
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdateMenuBurgerRequestDTO request
    ) {
        var command = mapper.toUpdateMenuBurgerCommand(idBurger, request);
        Burger updated = updateMenuBurger.handle(command);
        MenuBurgerResponseDTO response = mapper.toMenuResponse(updated);
        return ResponseEntity.ok(response);
    }

    // ========= ELIMINAR BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @DeleteMapping("/menu/{idBurger}")
    public ResponseEntity<MessageResponseDTO> deleteMenuBurger(@PathVariable Integer idBurger) {
        deleteMenuBurger.handle(idBurger);

        MessageResponseDTO response = new MessageResponseDTO(
                "Hamburguesa de menú eliminada  con el ID:"+idBurger,
                true
        );

        return ResponseEntity.ok(response);
    }

    // ========= LISTAR BURGERS DE MENÚ (paginado) =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @GetMapping("/menu")
    public ResponseEntity<MenuBurgerPageResponseDTO> listMenuBurgers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idBurger") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> burgerPage = listBurgers.handle(pagination);
        MenuBurgerPageResponseDTO responseDTO = mapper.toMenuBurgerPageResponseDTO(burgerPage);
        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @PatchMapping("/menu/{idBurger}/price")
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurgerPrice(
            @PathVariable Integer idBurger,
            @RequestBody UpdatePriceRequestDTO request
    ) {
        Burger updated = updateMenuBurgerPrice.handle(idBurger, request.newPrice());
        return ResponseEntity.ok(mapper.toMenuResponse(updated));
    }

    // ========= BUSCAR MENÚ =========
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @GetMapping("/menu/search")
    public ResponseEntity<MenuBurgerPageResponseDTO> searchMenuBurgers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        SearchMenuBurgersQuery query = new SearchMenuBurgersQuery(name);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);

        PageResponse<Burger> result = searchMenuBurgers.search(query, pagination);
        MenuBurgerPageResponseDTO responseDTO = mapper.toMenuBurgerPageResponseDTO(result);

        return ResponseEntity.ok(responseDTO);
    }





    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    @GetMapping("/{idBurger}")
    public ResponseEntity<MenuBurgerResponseDTO> getById(@PathVariable Integer idBurger) {
        Burger burger = getBurgerById.execute(idBurger); // o handle, según tu puerto
        return ResponseEntity.ok(mapper.toMenuResponse(burger));
    }
}
