package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
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
            BurgerRestDtoMapper mapper
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
    }

    // ==================== CREAR BURGER DE MENÚ ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Crear hamburguesa de menú",
            description = "Crea una nueva hamburguesa de menú con imagen opcional. " +
                    "La imagen se procesa de forma asíncrona."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Hamburguesa creada exitosamente",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o imagen corrupta",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos de administrador",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Ya existe una hamburguesa con ese nombre",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "413", description = "Imagen excede 5MB",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> createMenuBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "Datos de la hamburguesa (JSON)", required = true)
            @Valid @RequestPart("data") CreateMenuBurgerRequestDTO dto,
            @Parameter(description = "Imagen de la hamburguesa (JPG, PNG, WEBP, máx 5MB)")
            @RequestPart(value = "burgerImage", required = false) MultipartFile burgerImage
    ) {
        Integer adminUserId = userDetails.getId();
        boolean hasImage = (burgerImage != null && !burgerImage.isEmpty());

        logger.info(" POST /api/admin/burgers/menu - Admin ID: {}, Imagen: {}",
                adminUserId, hasImage ? "Sí" : "No");

        var command = mapper.toCreateBurgerCommand(dto, burgerImage, adminUserId);
        Burger burger = createMenuBurger.handle(command);

        logger.info(" Hamburguesa de menú creada: ID={}, isFavorite={}",
                burger.getIdBurger(), burger.isFavorite());

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(burger);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== ACTUALIZAR BURGER DE MENÚ ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PutMapping("/menu/{idBurger}")
    @Operation(
            summary = "Actualizar hamburguesa de menú",
            description = "Actualiza los datos de la hamburguesa (nombre, descripción, ingredientes, etc). " +
                    "Para actualizar la imagen usar PUT /api/admin/burgers/menu/{idBurger}/image"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Nombre duplicado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdateMenuBurgerRequestDTO request
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info(" PUT /api/admin/burgers/menu/{} - Admin ID: {}", idBurger, adminUserId);

        var command = mapper.toUpdateMenuBurgerCommand(idBurger, request, adminUserId);
        Burger updated = updateMenuBurger.handle(command);

        logger.info(" Hamburguesa actualizada: ID={}", idBurger);

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    // ==================== ACTUALIZAR IMAGEN ====================
// ==================== ACTUALIZAR IMAGEN ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping(value = "/menu/{idBurger}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Actualizar imagen de hamburguesa",
            description = "Actualiza únicamente la imagen de la hamburguesa. " +
                    "La subida a S3 es asíncrona, por lo que la URL puede no estar disponible inmediatamente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen aceptada para procesamiento",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Imagen inválida o corrupta",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "413", description = "Imagen excede 5MB",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurgerImage(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger,
            @Parameter(description = "Nueva imagen (JPG, PNG, WEBP, máx 5MB)", required = true)
            @RequestPart("burgerImage") MultipartFile burgerImage
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info(" PATCH /api/admin/burgers/menu/{}/image - Admin ID: {}", idBurger, adminUserId);

        FileData imageData = FileData.from(burgerImage);
        UpdateMenuBurgerImageCommand command = new UpdateMenuBurgerImageCommand(
                idBurger,
                imageData,
                adminUserId
        );

        Burger updated = updateMenuBurgerImage.handle(command);

        logger.info("Solicitud de actualización de imagen procesada: ID={}", idBurger);

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(updated);
        return ResponseEntity.ok(response);
    }


    // ==================== TOGGLE FAVORITA ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/menu/{idBurger}/isFavorite")
    @Operation(
            summary = "Marcar/desmarcar como destacada",
            description = "Cambia el estado de favorita de la hamburguesa " +
                    "(destacada en el menú del restaurante)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> toggleFavorite(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger,
            @Parameter(description = "true = marcar como destacada, false = desmarcar", required = true)
            @RequestParam Boolean isFavorite
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info(" PATCH /api/admin/burgers/menu/{}/isFavorite - isFavorite={}",
                idBurger, isFavorite);

        Burger burger = toggleMenuBurgerFavorite.handle(idBurger, isFavorite, adminUserId);

        logger.info(" Estado de favorita actualizado: ID={}, isFavorite={}",
                idBurger, burger.isFavorite());

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(burger);
        return ResponseEntity.ok(response);
    }

    // ==================== ACTUALIZAR PRECIO ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping("/menu/{idBurger}/price")
    @Operation(
            summary = "Actualizar precio de hamburguesa",
            description = "Cambia el precio final de la hamburguesa sin modificar otros datos"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Precio actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Precio inválido (debe ser mayor a 0)",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurgerPrice(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdatePriceRequestDTO request
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info(" PATCH /api/admin/burgers/menu/{}/price - newPrice={}",
                idBurger, request.newPrice());

        Burger updated = updateMenuBurgerPrice.handle(idBurger, request.newPrice());

        logger.info(" Precio actualizado: ID={}, newPrice={}", idBurger, request.newPrice());

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    // ==================== ELIMINAR ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @DeleteMapping("/menu/{idBurger}")
    @Operation(
            summary = "Eliminar hamburguesa de menú",
            description = "Realiza soft delete (marca como eliminada sin borrar físicamente)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa eliminada exitosamente",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MessageResponseDTO> deleteMenuBurger(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger
    ) {
        Integer adminUserId = userDetails.getId();
        logger.info("DELETE /api/admin/burgers/menu/{} - Admin ID: {}", idBurger, adminUserId);

        deleteMenuBurger.handle(idBurger, adminUserId);

        logger.info(" Hamburguesa eliminada: ID={}", idBurger);

        return ResponseEntity.ok(new MessageResponseDTO(
                "Hamburguesa de menú eliminada exitosamente",
                true
        ));
    }

    // ==================== LISTAR ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/menu")
    @Operation(
            summary = "Listar hamburguesas de menú",
            description = "Retorna lista paginada de todas las hamburguesas del menú"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = MenuBurgerPageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerPageResponseDTO> listMenuBurgers(
            @Parameter(description = "Número de página (inicia en 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Dirección de ordenamiento (ASC/DESC)")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        logger.info(" GET /api/admin/burgers/menu - page={}, size={}", page, size);

        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> burgerPage = listBurgers.handle(pagination);

        MenuBurgerPageResponseDTO response = mapper.toMenuBurgerPageResponseDTO(burgerPage);
        return ResponseEntity.ok(response);
    }

    // ==================== BUSCAR ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/menu/search")
    @Operation(
            summary = "Buscar hamburguesas por nombre",
            description = "Busca hamburguesas que contengan el texto especificado en el nombre"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda completada",
                    content = @Content(schema = @Schema(implementation = MenuBurgerPageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerPageResponseDTO> searchMenuBurgers(
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
        logger.info(" GET /api/admin/burgers/menu/search - name='{}'", name);

        SearchMenuBurgersQuery query = new SearchMenuBurgersQuery(name);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> result = searchMenuBurgers.search(query, pagination);

        MenuBurgerPageResponseDTO response = mapper.toMenuBurgerPageResponseDTO(result);
        return ResponseEntity.ok(response);
    }

    // ==================== OBTENER POR ID ====================

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/{idBurger}")
    @Operation(
            summary = "Obtener hamburguesa por ID",
            description = "Retorna los detalles completos de una hamburguesa específica"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hamburguesa encontrada",
                    content = @Content(schema = @Schema(implementation = MenuBurgerResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Hamburguesa no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<MenuBurgerResponseDTO> getById(
            @Parameter(description = "ID de la hamburguesa", required = true)
            @PathVariable Integer idBurger
    ) {
        logger.info(" GET /api/admin/burgers/{}", idBurger);

        Burger burger = getBurgerById.execute(idBurger);
        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(burger);

        return ResponseEntity.ok(response);
    }
}
