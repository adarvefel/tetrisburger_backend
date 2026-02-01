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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/burgers")
@Tag(name = "User Burgers", description = "Gestión de hamburguesas personalizadas del cliente")
@SecurityRequirement(name = "bearerAuth")
public class UserBurgerController {

    private static final Logger logger = LoggerFactory.getLogger(UserBurgerController.class);

    private final CreateCustomBurger createCustomBurger;
    private final ListCustomBurgersByUser listCustomBurgersByUser;
    private final UpdateCustomBurger updateCustomBurger;
    private final DeleteCustomBurger deleteCustomBurger;
    private final MarkCustomBurgerAsFavorite markCustomBurgerAsFavorite;
    private final UnmarkCustomBurgerAsFavorite unmarkCustomBurgerAsFavorite;
    private final SearchCustomBurgers searchCustomBurgers;
    private final BurgerRestDtoMapper mapper;

    public UserBurgerController(
            CreateCustomBurger createCustomBurger,
            ListCustomBurgersByUser listCustomBurgersByUser,
            UpdateCustomBurger updateCustomBurger,
            DeleteCustomBurger deleteCustomBurger,
            MarkCustomBurgerAsFavorite markCustomBurgerAsFavorite,
            UnmarkCustomBurgerAsFavorite unmarkCustomBurgerAsFavorite,
            SearchCustomBurgers searchCustomBurgers,
            BurgerRestDtoMapper mapper
    ) {
        this.createCustomBurger = createCustomBurger;
        this.listCustomBurgersByUser = listCustomBurgersByUser;
        this.updateCustomBurger = updateCustomBurger;
        this.deleteCustomBurger = deleteCustomBurger;
        this.markCustomBurgerAsFavorite = markCustomBurgerAsFavorite;
        this.unmarkCustomBurgerAsFavorite = unmarkCustomBurgerAsFavorite;
        this.searchCustomBurgers = searchCustomBurgers;
        this.mapper = mapper;
    }

    // ========= CREAR CUSTOM BURGER =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PostMapping("/custom")
    @Operation(
            summary = "Crear hamburguesa personalizada",
            description = "Permite al cliente crear su propia hamburguesa seleccionando ingredientes"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hamburguesa creada",
                    content = @Content(schema = @Schema(implementation = BurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<BurgerResponseDTO> createCustom(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateCustomBurgerRequestDTO request
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        logger.info("POST /api/burgers/custom - Usuario ID: {}", idUser);

        CreateCustomBurgerCommand command = mapper.toCreateCustomBurgerCommand(request, null, idUser);
        Burger burger = createCustomBurger.handle(command);

        logger.info("Hamburguesa custom creada: idBurger={}, idUser={}", burger.getIdBurger(), idUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toBurgerResponseDTO(burger));
    }

    // ========= LISTAR MIS CUSTOM BURGERS =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @GetMapping("/custom/mine")
    @Operation(
            summary = "Listar mis hamburguesas personalizadas",
            description = "Retorna lista paginada de las hamburguesas custom del cliente autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida",
                    content = @Content(schema = @Schema(implementation = BurgerPageResponseDTO.class)))
    })
    public ResponseEntity<BurgerPageResponseDTO> listMyCustomBurgers(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección (ASC/DESC)")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        logger.info("GET /api/burgers/custom/mine - Usuario ID: {}, page={}, size={}", idUser, page, size);

        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> burgerPage = listCustomBurgersByUser.handle(idUser, pagination);
        BurgerPageResponseDTO responseDTO = mapper.toBurgerPageResponseDTO(burgerPage);

        return ResponseEntity.ok(responseDTO);
    }

    // ========= ACTUALIZAR CUSTOM BURGER =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PutMapping("/custom/{idBurger}")
    @Operation(
            summary = "Actualizar mi hamburguesa personalizada",
            description = "Permite al cliente actualizar su propia hamburguesa custom"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa actualizada",
                    content = @Content(schema = @Schema(implementation = BurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso (no es tu burger)",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<BurgerResponseDTO> updateMyCustomBurger(
            @PathVariable Integer idBurger,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateCustomBurgerRequestDTO request
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        logger.info("PUT /api/burgers/custom/{} - Usuario ID: {}", idBurger, idUser);

        UpdateCustomBurgerCommand command = mapper.toUpdateCustomBurgerCommand(idBurger, request, idUser);
        Burger updated = updateCustomBurger.handle(command);

        logger.info("Hamburguesa custom actualizada: idBurger={}", idBurger);

        return ResponseEntity.ok(mapper.toBurgerResponseDTO(updated));
    }

    // ========= ELIMINAR CUSTOM BURGER =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @DeleteMapping("/custom/{idBurger}")
    @Operation(
            summary = "Eliminar mi hamburguesa personalizada",
            description = "Elimina una hamburguesa custom del cliente (soft delete)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa eliminada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso (no es tu burger)",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> deleteMyCustomBurger(
            @PathVariable Integer idBurger,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        logger.info("DELETE /api/burgers/custom/{} - Usuario ID: {}", idBurger, idUser);

        DeleteCustomBurgerCommand command = new DeleteCustomBurgerCommand(idBurger, idUser);
        deleteCustomBurger.handle(command);

        logger.info("Hamburguesa custom eliminada: idBurger={}", idBurger);

        return ResponseEntity.ok(new MessageResponseDTO(
                "Hamburguesa eliminada exitosamente",
                true
        ));
    }

    // ========= MARCAR COMO FAVORITA =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PatchMapping("/custom/{idBurger}/favorite")
    @Operation(
            summary = "Marcar mi hamburguesa como favorita",
            description = "Marca una hamburguesa custom como favorita personal del cliente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marcada como favorita",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso (no es tu burger)",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> markAsFavorite(
            @PathVariable Integer idBurger,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        logger.info("PATCH /api/burgers/custom/{}/favorite - Usuario ID: {}", idBurger, idUser);

        markCustomBurgerAsFavorite.handle(idBurger, idUser);

        logger.info("Hamburguesa custom marcada como favorita: idBurger={}, idUser={}", idBurger, idUser);

        return ResponseEntity.ok(new MessageResponseDTO(
                "Hamburguesa marcada como favorita exitosamente",
                true
        ));
    }

    // ========= DESMARCAR COMO FAVORITA =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @DeleteMapping("/custom/{idBurger}/favorite")
    @Operation(
            summary = "Desmarcar mi hamburguesa como favorita",
            description = "Desmarca una hamburguesa custom como favorita personal del cliente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desmarcada como favorita",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso (no es tu burger)",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> unmarkAsFavorite(
            @PathVariable Integer idBurger,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        logger.info("DELETE /api/burgers/custom/{}/favorite - Usuario ID: {}", idBurger, idUser);

        unmarkCustomBurgerAsFavorite.handle(idBurger, idUser);

        logger.info("Hamburguesa custom desmarcada como favorita: idBurger={}, idUser={}", idBurger, idUser);

        return ResponseEntity.ok(new MessageResponseDTO(
                "Hamburguesa desmarcada como favorita exitosamente",
                true
        ));
    }

    // ========= BUSCAR MIS CUSTOM BURGERS =========

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @GetMapping("/custom/search")
    @Operation(
            summary = "Buscar mis hamburguesas personalizadas",
            description = "Busca hamburguesas custom del cliente por nombre"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda completada",
                    content = @Content(schema = @Schema(implementation = BurgerPageResponseDTO.class)))
    })
    public ResponseEntity<BurgerPageResponseDTO> searchMyCustomBurgers(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Texto de búsqueda") @RequestParam String name,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "DESC") String direction
    ) {
        Integer idUser = getUserIdFromDetails(userDetails);
        logger.info("GET /api/burgers/custom/search - Usuario ID: {}, name={}", idUser, name);

        SearchCustomBurgersQuery query = new SearchCustomBurgersQuery(idUser,name);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);

        PageResponse<Burger> result = searchCustomBurgers.search(query, pagination);
        BurgerPageResponseDTO responseDTO = mapper.toBurgerPageResponseDTO(result);

        return ResponseEntity.ok(responseDTO);
    }

    // ========= HELPER METHODS =========

    /**
     * Extrae el ID del usuario desde UserDetails
     */
    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new IllegalStateException("UserDetails no es CustomUserDetails");
    }
}
