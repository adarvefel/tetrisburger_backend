package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.DeleteCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchCustomBurgersQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/burgers")
public class UserBurgerController {

    private static final Logger logger = LoggerFactory.getLogger(UserBurgerController.class);

    private final CreateCustomBurger createCustomBurger;
    private final ListCustomBurgersByUser listCustomBurgersByUser;
    private final UpdateCustomBurger updateCustomBurger;
    private final DeleteCustomBurger deleteCustomBurger;
    private final MarkCustomBurgerAsFavorite markCustomBurgerAsFavorite;
    private final SearchCustomBurgers searchCustomBurgers;
    private final BurgerRestDtoMapper mapper;

    public UserBurgerController(CreateCustomBurger createCustomBurger, ListCustomBurgersByUser listCustomBurgersByUser, UpdateCustomBurger updateCustomBurger, DeleteCustomBurger deleteCustomBurger, MarkCustomBurgerAsFavorite markCustomBurgerAsFavorite, SearchCustomBurgers searchCustomBurgers, BurgerRestDtoMapper mapper) {
        this.createCustomBurger = createCustomBurger;
        this.listCustomBurgersByUser = listCustomBurgersByUser;
        this.updateCustomBurger = updateCustomBurger;
        this.deleteCustomBurger = deleteCustomBurger;
        this.markCustomBurgerAsFavorite = markCustomBurgerAsFavorite;
        this.searchCustomBurgers = searchCustomBurgers;
        this.mapper = mapper;
    }

    // ========= CUSTOM BURGER (cliente) =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PostMapping("/custom")
    public ResponseEntity<BurgerResponseDTO> createCustom(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateCustomBurgerRequestDTO request
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);

        CreateCustomBurgerCommand command = mapper.toCustomCommand(request, idUser);
        Burger burger = createCustomBurger.handle(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(burger));
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @GetMapping("/custom/mine")
    public ResponseEntity<BurgerPageResponseDTO> listMyCustomBurgers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);

        PageResponse<Burger> burgerPage = listCustomBurgersByUser.handle(idUser, pagination);
        BurgerPageResponseDTO responseDTO = mapper.toBurgerPageResponseDTO(burgerPage);

        return ResponseEntity.ok(responseDTO);
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PutMapping("/custom/{idBurger}")
    public ResponseEntity<BurgerResponseDTO> updateMyCustomBurger(
            @PathVariable Integer idBurger,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateCustomBurgerRequestDTO request
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);

        UpdateCustomBurgerCommand command =
                mapper.toUpdateCustomBurgerCommand(idBurger, idUser, request);

        Burger updated = updateCustomBurger.handle(command);

        return ResponseEntity.ok(mapper.toResponse(updated));
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @DeleteMapping("/custom/{idBurger}")
    public ResponseEntity<MessageResponseDTO> deleteMyCustomBurger(
            @PathVariable Integer idBurger,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        deleteCustomBurger.handle(new DeleteCustomBurgerCommand(idBurger, idUser));

        return ResponseEntity.ok(
                new MessageResponseDTO("Burger custom eliminada correctamente con el ID:"+idBurger
                        , true)
        );
    }


    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PostMapping("/custom/{idBurger}/favorite")
    public ResponseEntity<MessageResponseDTO> markMyCustomBurgerAsFavorite(
            @PathVariable Integer idBurger,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        markCustomBurgerAsFavorite.handle(idBurger, idUser);

        return ResponseEntity.ok(
                new MessageResponseDTO("Burger marcada como favorita", true)
        );
    }

    @GetMapping("/custom/search")
    public ResponseEntity<BurgerPageResponseDTO> searchCustomBurgers(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            Authentication authentication  // ← Del token JWT
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer idUser = userDetails.getId();

        SearchCustomBurgersQuery query = new SearchCustomBurgersQuery(idUser, name);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);

        PageResponse<Burger> result = searchCustomBurgers.search(query, pagination);
        return ResponseEntity.ok(mapper.toBurgerPageResponseDTO(result));
    }




    // ========= HELPERS =========

    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails cud) {
            return cud.getId();
        }
        throw new IllegalStateException("UserDetails no es CustomUserDetails");
    }
}
