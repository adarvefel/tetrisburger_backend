package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurger;
import com.tetris.tetrisburger_backend.domain.model.FavoriteBurgerDetail;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetBurgerById;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.AddFavoriteBurger;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.GetFavoriteBurgersByUser;
import com.tetris.tetrisburger_backend.domain.port.in.favoriteburger.RemoveFavoriteBurger;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.DeleteResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.favoriteburger.FavoriteBurgerSimpleResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.FavoriteBurgerRestDtoMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "Gestión de hamburguesas favoritas del usuario")
public class FavoriteBurgerController extends AuthenticatedController {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteBurgerController.class);

    private final AddFavoriteBurger addFavoriteBurger;
    private final GetBurgerById getBurgerById;
    private final RemoveFavoriteBurger removeFavoriteBurger;
    private final GetFavoriteBurgersByUser getFavoriteBurgersByUser;
    private final FavoriteBurgerRestDtoMapper mapper;

    public FavoriteBurgerController(AddFavoriteBurger addFavoriteBurger, GetBurgerById getBurgerById, RemoveFavoriteBurger removeFavoriteBurger, GetFavoriteBurgersByUser getFavoriteBurgersByUser, FavoriteBurgerRestDtoMapper mapper) {
        this.addFavoriteBurger = addFavoriteBurger;
        this.getBurgerById = getBurgerById;
        this.removeFavoriteBurger = removeFavoriteBurger;
        this.getFavoriteBurgersByUser = getFavoriteBurgersByUser;
        this.mapper = mapper;
    }

    // ==================== AGREGAR FAVORITO ====================

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT')")
    public ResponseEntity<FavoriteBurgerSimpleResponseDTO> add(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FavoriteBurgerRequestDTO dto
    ) {
        Integer idUser = getUserId(userDetails);
        logger.info("POST /api/favorites - Usuario: {} | Hamburguesa: {}", idUser, dto.idBurger());

        FavoriteBurger saved = addFavoriteBurger.handle(idUser, dto.idBurger());

        logger.info("Favorito agregado: ID {} | Usuario: {} | Hamburguesa: {}",
                saved.getIdFavorite(), idUser, dto.idBurger());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toSimpleResponseDTO(saved));
    }

    // ==================== ELIMINAR FAVORITO ====================

    @DeleteMapping("/{idBurger}")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT')")
    public ResponseEntity<DeleteResponseDTO> remove(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer idBurger
    ) {
        Integer idUser = getUserId(userDetails);
        logger.info("DELETE /api/favorites/{} - Usuario: {}", idBurger, idUser);

        // Obtener burger ANTES de eliminar
        Burger burger = getBurgerById.execute(idBurger);
        removeFavoriteBurger.handle(idUser, idBurger);

        String mensaje = !burger.isOnMenu()
                ? "Hamburguesa personalizada eliminada de favoritos y de tus creaciones"
                : "Hamburguesa eliminada de favoritos";

        logger.info("Favorito eliminado: Usuario: {} | Hamburguesa: {}", idUser, idBurger);

        return ResponseEntity.ok(new DeleteResponseDTO(
                mensaje,
                true,
                new DeleteResponseDTO.DeletedResourceDTO(idBurger, burger.getName())
        ));
    }

    // ==================== LISTAR FAVORITOS ====================

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT')")
    public ResponseEntity<List<FavoriteBurgerResponseDTO>> getAll(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer idUser = getUserId(userDetails);
        logger.info("GET /api/favorites - Usuario: {}", idUser);

        List<FavoriteBurgerDetail> favorites = getFavoriteBurgersByUser.handle(idUser); // ← FavoriteBurgerDetail

        return ResponseEntity.ok(mapper.toResponseDTOList(favorites));
    }

}
