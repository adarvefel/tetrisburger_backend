package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateCustomBurger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.CreateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.DeleteCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchCustomBurgersQuery;
import com.tetris.tetrisburger_backend.domain.port.in.burger.user.*;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final UpdateCustomBurgerImage updateCustomBurgerImage;
    private final SearchCustomBurgers searchCustomBurgers;
    private final BurgerRestDtoMapper mapper;

    public UserBurgerController(CreateCustomBurger createCustomBurger, ListCustomBurgersByUser listCustomBurgersByUser, UpdateCustomBurger updateCustomBurger, DeleteCustomBurger deleteCustomBurger, MarkCustomBurgerAsFavorite markCustomBurgerAsFavorite, UnmarkCustomBurgerAsFavorite unmarkCustomBurgerAsFavorite, UpdateCustomBurgerImage updateCustomBurgerImage, SearchCustomBurgers searchCustomBurgers, BurgerRestDtoMapper mapper) {
        this.createCustomBurger = createCustomBurger;
        this.listCustomBurgersByUser = listCustomBurgersByUser;
        this.updateCustomBurger = updateCustomBurger;
        this.deleteCustomBurger = deleteCustomBurger;
        this.markCustomBurgerAsFavorite = markCustomBurgerAsFavorite;
        this.unmarkCustomBurgerAsFavorite = unmarkCustomBurgerAsFavorite;
        this.updateCustomBurgerImage = updateCustomBurgerImage;
        this.searchCustomBurgers = searchCustomBurgers;
        this.mapper = mapper;
    }

    // ==================== CREAR CUSTOM BURGER ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PostMapping(value = "/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear hamburguesa personalizada",
            description = "Permite al cliente crear su propia hamburguesa con imagen opcional. " +
                    "El precio se calcula automáticamente según los ingredientes elegidos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hamburguesa creada exitosamente",
                    content = @Content(schema = @Schema(implementation = BurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o ingredientes sin stock",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos de cliente",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "413", description = "Imagen excede 5MB",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<BurgerResponseDTO> createCustom(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart("data") CreateCustomBurgerRequestDTO request,
            @RequestPart(value = "burgerImage", required = false) MultipartFile burgerImage
    ) {
        Integer userId = userDetails.getId();
        boolean hasImage = burgerImage != null && !burgerImage.isEmpty();

        logger.info("POST /api/burgers/custom - Usuario ID: {}, Imagen: {}", userId, hasImage);

        CreateCustomBurgerCommand command = mapper.toCreateCustomBurgerCommand(request, burgerImage, userId);
        Burger burger = createCustomBurger.handle(command);

        logger.info("Hamburguesa personalizada creada: ID={}, precio=${}, imageStatus={}",
                burger.getIdBurger(), burger.getFinalPrice(), burger.getImageStatus());

        BurgerResponseDTO response = mapper.toBurgerResponseDTO(burger);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== LISTAR MIS CUSTOM BURGERS ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @GetMapping("/custom/mine")
    @Operation(
            summary = "Listar mis hamburguesas personalizadas",
            description = "Retorna lista paginada de todas las hamburguesas personalizadas del cliente autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = BurgerPageResponseDTO.class)))
    })
    public ResponseEntity<BurgerPageResponseDTO> listMyCustomBurgers(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección de ordenamiento (ASC/DESC)")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Integer userId = userDetails.getId();
        logger.info("GET /api/burgers/custom/mine - Usuario ID: {}, page={}, size={}", userId, page, size);

        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> burgerPage = listCustomBurgersByUser.handle(userId, pagination);

        BurgerPageResponseDTO response = mapper.toBurgerPageResponseDTO(burgerPage);
        return ResponseEntity.ok(response);
    }

    // ==================== ACTUALIZAR CUSTOM BURGER ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PatchMapping("/custom/{idBurger}")
    @Operation(
            summary = "Actualizar mi hamburguesa personalizada",
            description = "Permite al cliente actualizar su propia hamburguesa personalizada. " +
                    "Solo puede actualizar sus propias hamburguesas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = BurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o ingredientes sin stock",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para actualizar esta hamburguesa",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<BurgerResponseDTO> updateMyCustomBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdateCustomBurgerRequestDTO request
    ) {
        Integer userId = userDetails.getId();
        logger.info("PUT /api/burgers/custom/{} - Usuario ID: {}", idBurger, userId);

        UpdateCustomBurgerCommand command = mapper.toUpdateCustomBurgerCommand(idBurger, request, userId);
        Burger updated = updateCustomBurger.handle(command);

        logger.info("Hamburguesa personalizada actualizada: ID={}, nuevoPrecio=${}", idBurger, updated.getFinalPrice());

        BurgerResponseDTO response = mapper.toBurgerResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    // ==================== ELIMINAR CUSTOM BURGER ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @DeleteMapping("/custom/{idBurger}")
    @Operation(
            summary = "Eliminar mi hamburguesa personalizada",
            description = "Elimina una hamburguesa personalizada del cliente (soft delete). " +
                    "Solo puede eliminar sus propias hamburguesas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa eliminada exitosamente",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para eliminar esta hamburguesa",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> deleteMyCustomBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger
    ) {
        Integer userId = userDetails.getId();
        logger.info("DELETE /api/burgers/custom/{} - Usuario ID: {}", idBurger, userId);

        DeleteCustomBurgerCommand command = new DeleteCustomBurgerCommand(idBurger, userId);
        deleteCustomBurger.handle(command);

        logger.info("Hamburguesa personalizada eliminada: ID={}", idBurger);

        return ResponseEntity.ok(new MessageResponseDTO("Hamburguesa eliminada exitosamente", true));
    }

    // ==================== MARCAR COMO FAVORITA ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PatchMapping("/custom/{idBurger}/mark")
    @Operation(
            summary = "Marcar hamburguesa como favorita",
            description = "Marca una hamburguesa personalizada como favorita personal del cliente. " +
                    "Útil para acceso rápido a recetas favoritas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Marcada como favorita exitosamente",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para marcar esta hamburguesa",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> markAsFavorite(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger
    ) {
        Integer userId = userDetails.getId();
        logger.info("PATCH /api/burgers/custom/{}/isFavorite - Usuario ID: {}", idBurger, userId);

        markCustomBurgerAsFavorite.handle(idBurger, userId);

        logger.info("Hamburguesa marcada como favorita: ID={}", idBurger);

        return ResponseEntity.ok(new MessageResponseDTO("Hamburguesa marcada como favorita exitosamente", true));
    }

    // ==================== DESMARCAR COMO FAVORITA ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PatchMapping("/custom/{idBurger}/unMark")
    @Operation(
            summary = "Desmarcar hamburguesa como favorita",
            description = "Desmarca una hamburguesa personalizada como favorita personal del cliente"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desmarcada exitosamente",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para desmarcar esta hamburguesa",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> unmarkAsFavorite(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger
    ) {
        Integer userId = userDetails.getId();
        logger.info("DELETE /api/burgers/custom/{}/isFavorite - Usuario ID: {}", idBurger, userId);

        unmarkCustomBurgerAsFavorite.handle(idBurger, userId);

        logger.info("Hamburguesa desmarcada como favorita: ID={}", idBurger);

        return ResponseEntity.ok(new MessageResponseDTO("Hamburguesa desmarcada como favorita exitosamente", true));
    }

    // ==================== ACTUALIZAR IMAGEN CUSTOM BURGER ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @PatchMapping(value = "/custom/{idBurger}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar imagen de hamburguesa personalizada",
            description = "Permite al cliente actualizar solo la imagen de su hamburguesa personalizada. " +
                    "La imagen anterior en S3 será reemplazada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = BurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Imagen inválida o excede 5MB",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para actualizar esta hamburguesa",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<BurgerResponseDTO> updateCustomBurgerImage(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger,
            @Parameter(description = "Nueva imagen de la hamburguesa", required = true)
            @RequestPart("burgerImage") MultipartFile burgerImage
    ) {
        Integer userId = userDetails.getId();
        logger.info("PATCH /api/burgers/custom/{}/image - Usuario ID: {}, Imagen: {}",
                idBurger, userId, burgerImage.getOriginalFilename());

        UpdateCustomBurgerImageCommand command = new UpdateCustomBurgerImageCommand(
                idBurger,
                FileData.from(burgerImage),
                userId
        );

        Burger updated = updateCustomBurgerImage.handle(command);

        logger.info("Imagen actualizada: ID={}, imageStatus={}", idBurger, updated.getImageStatus());

        BurgerResponseDTO response = mapper.toBurgerResponseDTO(updated);
        return ResponseEntity.ok(response);
    }


    // ==================== BUSCAR MIS CUSTOM BURGERS ====================

    @PreAuthorize("hasAuthority('ROLE_CLIENT')")
    @GetMapping("/custom/search")
    @Operation(
            summary = "Buscar mis hamburguesas personalizadas",
            description = "Busca hamburguesas personalizadas del cliente por nombre. " +
                    "Solo busca en las hamburguesas del usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente",
                    content = @Content(schema = @Schema(implementation = BurgerPageResponseDTO.class)))
    })
    public ResponseEntity<BurgerPageResponseDTO> searchMyCustomBurgers(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Texto de búsqueda", required = true)
            @RequestParam String name,
            @Parameter(description = "Número de página")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección de ordenamiento")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Integer userId = userDetails.getId();
        logger.info("GET /api/burgers/custom/search - Usuario ID: {}, name='{}'", userId, name);

        SearchCustomBurgersQuery query = new SearchCustomBurgersQuery(userId, name);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);

        PageResponse<Burger> result = searchCustomBurgers.search(query, pagination);

        BurgerPageResponseDTO response = mapper.toBurgerPageResponseDTO(result);
        return ResponseEntity.ok(response);
    }
}
