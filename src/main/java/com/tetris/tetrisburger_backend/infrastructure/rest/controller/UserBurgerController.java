package com.tetris.tetrisburger_backend.infrastructure.rest.controller;


import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.GetFeaturedBurgers;
import com.tetris.tetrisburger_backend.domain.port.in.burger.ListBurgerIngredients;
import com.tetris.tetrisburger_backend.domain.port.in.burger.SearchIngredients;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.CreateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.client.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.user.*;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.MenuBurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.BurgerResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.CreateCustomBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.client.UpdateCustomBurgerRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.BurgerIngredientListDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/burgers")
@Tag(name = "Client Burgers", description = "Gestión de hamburguesas personalizadas del cliente")
@SecurityRequirement(name = "bearerAuth")
public class UserBurgerController {

    private static final Logger logger = LoggerFactory.getLogger(UserBurgerController.class);

    private final CreateCustomBurger createCustomBurger;
    private final ListBurgerIngredients listBurgerIngredients;
    private final SearchIngredients searchIngredients;
    private final UpdateCustomBurger updateCustomBurger;
    private final ProductRestDtoMapper productRestDtoMapper;
    private final GetFeaturedBurgers getFeaturedBurgers;
    private final BurgerRestDtoMapper mapper;

    public UserBurgerController(CreateCustomBurger createCustomBurger, ListBurgerIngredients listBurgerIngredients, SearchIngredients searchIngredients, UpdateCustomBurger updateCustomBurger, ProductRestDtoMapper productRestDtoMapper, GetFeaturedBurgers getFeaturedBurgers, BurgerRestDtoMapper mapper) {
        this.createCustomBurger = createCustomBurger;
        this.listBurgerIngredients = listBurgerIngredients;
        this.searchIngredients = searchIngredients;
        this.updateCustomBurger = updateCustomBurger;
        this.productRestDtoMapper = productRestDtoMapper;
        this.getFeaturedBurgers = getFeaturedBurgers;
        this.mapper = mapper;
    }

    // ==================== CREAR CUSTOM BURGER ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PostMapping("/custom")
    public ResponseEntity<BurgerResponseDTO> createCustomBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateCustomBurgerRequestDTO dto
    ) {
        Integer userId = userDetails.getId();
        CreateCustomBurgerCommand command = mapper.toCreateCustomBurgerCommand(dto, userId);
        Burger burger = createCustomBurger.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toBurgerResponseDTO(burger));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENT')")
    @GetMapping("/ingredients/search")
    @Operation(
            summary = "Buscar ingredientes por nombre",
            description = "Busca productos de tipo INGREDIENT disponibles y no eliminados que contengan el nombre especificado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda completada",
                    content = @Content(schema = @Schema(implementation = BurgerIngredientListDTO.class)))
    })
    public ResponseEntity<BurgerIngredientListDTO> searchIngredients(
            @Parameter(description = "Texto de búsqueda por nombre")
            @RequestParam(required = false) String name,
            @Parameter(description = "Número de página")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("GET /api/admin/burgers/ingredients/search - name='{}'", name);

        PaginationRequest pagination = new PaginationRequest(page, size);
        PageResponse<Product> result = searchIngredients.handle(name, pagination);

        return ResponseEntity.ok(productRestDtoMapper.toBurgerIngredientListDTO(result));
    }


    // ==================== ACTUALIZAR CUSTOM BURGER ====================
    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PutMapping("/custom/{idBurger}")
    @Operation(summary = "Actualizar hamburguesa personalizada")
    public ResponseEntity<BurgerResponseDTO> updateCustomBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdateCustomBurgerRequestDTO dto
    ) {
        Integer userId = userDetails.getId();
        UpdateCustomBurgerCommand command = mapper.toUpdateCustomBurgerCommand(idBurger, dto, userId);
        Burger burger = updateCustomBurger.handle(command);
        return ResponseEntity.ok(mapper.toBurgerResponseDTO(burger));
    }

    @GetMapping("/featured")
    @Operation(
            summary = "Listar hamburguesas destacadas",
            description = "Retorna todas las hamburguesas marcadas como destacadas y disponibles"
    )
    public ResponseEntity<List<MenuBurgerResponseDTO>> getFeaturedBurgers() {
        logger.info("GET /api/admin/burgers/menu/featured");

        List<Burger> burgers = getFeaturedBurgers.handle();
        List<MenuBurgerResponseDTO> response = burgers.stream()
                .map(mapper::toMenuBurgerResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }



}
