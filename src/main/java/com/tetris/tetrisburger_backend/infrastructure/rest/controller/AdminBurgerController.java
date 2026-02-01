package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/burgers")
@Tag(name = "Admin Burgers", description = "Gestión de hamburguesas de menú (Administrador)")
@SecurityRequirement(name = "bearerAuth")
public class AdminBurgerController {

    private static final Logger logger = LoggerFactory.getLogger(AdminBurgerController.class);

    private final CreateMenuBurger createMenuBurger;
    private final GetBurgerById getBurgerById;
    private final ListBurgers listBurgers;
    private final UpdateMenuBurger updateMenuBurger;
    private final UpdateMenuBurgerImage updateMenuBurgerImage;
    private final DeleteMenuBurger deleteMenuBurger;
    private final UpdateMenuBurgerPrice updateMenuBurgerPrice;
    private final SearchMenuBurgers searchMenuBurgers;
    private final ToggleMenuBurgerFavorite toggleMenuBurgerFavorite;
    private final BurgerRestDtoMapper mapper;
    private final ImageStoragePort imageStoragePort;

    public AdminBurgerController(
            CreateMenuBurger createMenuBurger,
            GetBurgerById getBurgerById,
            ListBurgers listBurgers,
            UpdateMenuBurger updateMenuBurger,
            UpdateMenuBurgerImage updateMenuBurgerImage,
            DeleteMenuBurger deleteMenuBurger,
            UpdateMenuBurgerPrice updateMenuBurgerPrice,
            SearchMenuBurgers searchMenuBurgers,
            ToggleMenuBurgerFavorite toggleMenuBurgerFavorite,
            BurgerRestDtoMapper mapper,
            ImageStoragePort imageStoragePort
    ) {
        this.createMenuBurger = createMenuBurger;
        this.getBurgerById = getBurgerById;
        this.listBurgers = listBurgers;
        this.updateMenuBurger = updateMenuBurger;
        this.updateMenuBurgerImage = updateMenuBurgerImage;
        this.deleteMenuBurger = deleteMenuBurger;
        this.updateMenuBurgerPrice = updateMenuBurgerPrice;
        this.searchMenuBurgers = searchMenuBurgers;
        this.toggleMenuBurgerFavorite = toggleMenuBurgerFavorite;
        this.mapper = mapper;
        this.imageStoragePort = imageStoragePort;
    }

    // ==================== HELPERS ====================

    /**
     * Resuelve la URL completa de la imagen desde el imageKey de S3
     */
    private String resolveImageUrlFromBurger(Burger burger) {
        if (burger == null || burger.getImageKey() == null || burger.getImageKey().isBlank()) {
            return null;
        }
        return imageStoragePort.getImageUrl(burger.getImageKey());
    }

    /**
     * Determina el estado de la imagen:
     * - NONE: Sin imagen
     * - PENDING: Subiendo a S3
     * - READY: Imagen disponible
     */
    private String resolveImageStatus(boolean imageWasSent, String imageUrlResolvedFromBurger) {
        if (imageWasSent) return "PENDING";
        if (imageUrlResolvedFromBurger != null) return "READY";
        return "NONE";
    }

    /**
     * Enriquece una respuesta de burger con imageUrl e imageStatus
     * (Crea un nuevo record con los campos adicionales)
     */
    private MenuBurgerResponseDTO enrichWithImageData(Burger burger, boolean imageWasSent) {
        MenuBurgerResponseDTO baseResponse = mapper.toMenuBurgerResponseDTO(burger);
        String imageUrl = resolveImageUrlFromBurger(burger);
        String imageStatus = resolveImageStatus(imageWasSent, imageUrl);

        // Como es un record, necesitas crear una nueva instancia con todos los campos
        return new MenuBurgerResponseDTO(
                baseResponse.idBurger(),
                baseResponse.name(),
                baseResponse.description(),
                baseResponse.basePrice(),
                baseResponse.finalPrice(),
                baseResponse.isOnMenu(),
                baseResponse.isFavorite(),
                baseResponse.isCustom(),
                baseResponse.availability(),
                imageUrl,           // imageUrl
                imageStatus,        // imageStatus
                baseResponse.timesOrdered(),
                baseResponse.ingredients(),
                baseResponse.createdAt(),
                baseResponse.updatedAt(),
                baseResponse.deletedAt(),
                baseResponse.createdBy(),
                baseResponse.updatedBy(),
                baseResponse.deletedBy()
        );
    }

    // ========= CREAR BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear hamburguesa de menú",
            description = "Crea una nueva hamburguesa de menú con imagen opcional. " +
                    "imageStatus: NONE (sin imagen), PENDING (subiendo), READY (disponible)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hamburguesa creada",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos o imagen inválidos",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "413", description = "Imagen excede 5MB",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> createMenuBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Datos de la hamburguesa (JSON)")
            @Valid @RequestPart("data") CreateMenuBurgerRequestDTO dto,
            @Parameter(description = "Imagen de la hamburguesa (opcional, máx 5MB)")
            @RequestPart(value = "burgerImage", required = false) MultipartFile burgerImage
    ) {
        Integer adminUserId = userDetails.getId();
        boolean imageWasSent = (burgerImage != null && !burgerImage.isEmpty());

        logger.info("POST /api/admin/burgers/menu - Usuario ID: {}, Imagen enviada: {}",
                adminUserId, imageWasSent);

        var command = mapper.toCreateBurgerCommand(dto, burgerImage, adminUserId);
        Burger burger = createMenuBurger.handle(command);

        logger.info("Hamburguesa de menú creada: idBurger={}, isFavorite={}, imageStatus={}",
                burger.getIdBurger(), burger.isFavorite(), imageWasSent ? "PENDING" : "NONE");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrichWithImageData(burger, imageWasSent));
    }

    // ========= ACTUALIZAR BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PutMapping("/menu/{idBurger}")
    @Operation(
            summary = "Actualizar hamburguesa de menú",
            description = "Actualiza datos de la hamburguesa (excepto imagen). " +
                    "Para actualizar imagen usar PUT /api/admin/burgers/menu/{idBurger}/image."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa actualizada",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdateMenuBurgerRequestDTO request
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info("PUT /api/admin/burgers/menu/{} - Usuario ID: {}", idBurger, adminUserId);

        var command = mapper.toUpdateMenuBurgerCommand(idBurger, request, adminUserId);
        Burger updated = updateMenuBurger.handle(command);

        logger.info("Hamburguesa actualizada: idBurger={}", idBurger);

        return ResponseEntity.ok(enrichWithImageData(updated, false));
    }

    // ========= ACTUALIZAR IMAGEN DE BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PutMapping(value = "/menu/{idBurger}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar imagen de hamburguesa de menú",
            description = "Actualiza únicamente la imagen. Responde con imageStatus PENDING " +
                    "(consulta GET para verificar cuando esté READY)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen aceptada (en proceso)",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Imagen inválida",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "413", description = "Imagen excede 5MB",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurgerImage(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa") @PathVariable Integer idBurger,
            @Parameter(description = "Nueva imagen (JPG, PNG, WEBP, máx 5MB)")
            @RequestPart("burgerImage") MultipartFile burgerImage
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info("PUT /api/admin/burgers/menu/{}/image - Usuario ID: {}", idBurger, adminUserId);

        FileData imageData = FileData.from(burgerImage);
        UpdateMenuBurgerImageCommand command = new UpdateMenuBurgerImageCommand(
                idBurger,
                imageData,
                adminUserId
        );

        Burger updated = updateMenuBurgerImage.handle(command);

        logger.info("Imagen de hamburguesa actualizada: idBurger={}", idBurger);

        return ResponseEntity.ok(enrichWithImageData(updated, true));
    }

    // ========= MARCAR/DESMARCAR COMO DESTACADA =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/menu/{idBurger}/favorite")
    @Operation(
            summary = "Marcar/desmarcar burger como destacada del menú",
            description = "Cambia el estado isFavorite de la burger del menú (recomendación del restaurante)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado de favorita actualizado",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> toggleFavorite(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger,
            @RequestParam Boolean isFavorite
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info("PATCH /api/admin/burgers/menu/{}/favorite - isFavorite={}, Usuario ID: {}",
                idBurger, isFavorite, adminUserId);

        Burger burger = toggleMenuBurgerFavorite.handle(idBurger, isFavorite, adminUserId);

        logger.info("Burger destacada actualizada: idBurger={}, isFavorite={}", idBurger, isFavorite);

        return ResponseEntity.ok(enrichWithImageData(burger, false));
    }

    // ========= ELIMINAR BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @DeleteMapping("/menu/{idBurger}")
    @Operation(
            summary = "Eliminar hamburguesa de menú",
            description = "Elimina una hamburguesa mediante soft delete"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa eliminada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> deleteMenuBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info("DELETE /api/admin/burgers/menu/{} - Usuario ID: {}", idBurger, adminUserId);

        deleteMenuBurger.handle(idBurger, adminUserId);

        logger.info("Hamburguesa eliminada: idBurger={}", idBurger);

        return ResponseEntity.ok(new MessageResponseDTO(
                "Hamburguesa de menú eliminada exitosamente con ID: " + idBurger,
                true
        ));
    }


    // ========= LISTAR BURGERS DE MENÚ (paginado) =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/menu")
    @Operation(
            summary = "Listar hamburguesas de menú",
            description = "Retorna lista paginada de hamburguesas de menú con imageStatus"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida",
                    content = @Content(schema = @Schema(implementation = MenuBurgerPageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerPageResponseDTO> listMenuBurgers(
            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "idBurger") String sortBy,
            @Parameter(description = "Dirección (ASC/DESC)")
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        logger.info("GET /api/admin/burgers/menu - page={}, size={}", page, size);

        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> burgerPage = listBurgers.handle(pagination);

        // Enriquecer cada burger con imageStatus
        List<MenuBurgerResponseDTO> enrichedItems = burgerPage.content().stream()
                .map(burger -> enrichWithImageData(burger, false))
                .collect(Collectors.toList());

        // Crear response con todos los campos del record
        MenuBurgerPageResponseDTO responseDTO = new MenuBurgerPageResponseDTO(
                enrichedItems,                  // content
                burgerPage.page(),              // page
                burgerPage.size(),              // size
                burgerPage.totalElements(),     // totalElements
                burgerPage.totalPages(),        // totalPages
                burgerPage.page() == 0,         // first (es la primera página?)
                burgerPage.page() == burgerPage.totalPages() - 1  // last (es la última página?)
        );

        return ResponseEntity.ok(responseDTO);
    }



    // ========= ACTUALIZAR PRECIO DE BURGER DE MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/menu/{idBurger}/price")
    @Operation(
            summary = "Actualizar precio de hamburguesa de menú",
            description = "Cambia el precio final sin modificar otros datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precio actualizado",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Precio inválido",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurgerPrice(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdatePriceRequestDTO request
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info("PATCH /api/admin/burgers/menu/{}/price - newPrice={}", idBurger, request.newPrice());

        Burger updated = updateMenuBurgerPrice.handle(idBurger, request.newPrice());

        logger.info("Precio actualizado: idBurger={}, newPrice={}", idBurger, request.newPrice());

        return ResponseEntity.ok(enrichWithImageData(updated, false));
    }

    // ========= BUSCAR MENÚ =========

    // ========= BUSCAR MENÚ =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/menu/search")
    @Operation(
            summary = "Buscar hamburguesas de menú",
            description = "Busca hamburguesas por nombre"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda completada",
                    content = @Content(schema = @Schema(implementation = MenuBurgerPageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerPageResponseDTO> searchMenuBurgers(
            @Parameter(description = "Texto de búsqueda") @RequestParam String name,
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "DESC") String direction
    ) {
        logger.info("GET /api/admin/burgers/menu/search - name={}", name);

        SearchMenuBurgersQuery query = new SearchMenuBurgersQuery(name);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> result = searchMenuBurgers.search(query, pagination);

        // Enriquecer resultados
        List<MenuBurgerResponseDTO> enrichedItems = result.content().stream()
                .map(burger -> enrichWithImageData(burger, false))
                .collect(Collectors.toList());

        // Crear response con todos los campos
        MenuBurgerPageResponseDTO responseDTO = new MenuBurgerPageResponseDTO(
                enrichedItems,                // content
                result.page(),                // page
                result.size(),                // size
                result.totalElements(),       // totalElements
                result.totalPages(),          // totalPages
                result.page() == 0,           // first
                result.page() == result.totalPages() - 1  // last
        );

        return ResponseEntity.ok(responseDTO);
    }


    // ========= OBTENER BURGER POR ID =========

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/{idBurger}")
    @Operation(
            summary = "Obtener hamburguesa por ID",
            description = "Retorna los detalles de una hamburguesa específica con imageStatus"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa encontrada",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> getById(@PathVariable Integer idBurger) {
        logger.info("GET /api/admin/burgers/{}", idBurger);

        Burger burger = getBurgerById.execute(idBurger);
        return ResponseEntity.ok(enrichWithImageData(burger, false));
    }
}
