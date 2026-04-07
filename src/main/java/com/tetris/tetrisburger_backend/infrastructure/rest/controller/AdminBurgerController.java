package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.burger.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.*;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.in.burger.query.SearchMenuBurgersQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.burger.admin.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.BurgerIngredientListDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.BurgerRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.rest.validator.ImageValidator;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private final CreateMenuBurger createMenuBurger;
    private final GetBurgerById getBurgerById;
    private final ListBurgers listBurgers;
    private final UpdateMenuBurger updateMenuBurger;
    private final UpdateMenuBurgerImage updateMenuBurgerImage;
    private final DeleteMenuBurger deleteMenuBurger;
    private final SearchMenuBurgers searchMenuBurgers;
    private final BurgerRestDtoMapper mapper;
    private final ListBurgerIngredients listBurgerIngredients;
    private final ProductRestDtoMapper productRestDtoMapper;
    private final SearchIngredients searchIngredients;
    private final ImageValidator imageValidator;

    public AdminBurgerController(CreateMenuBurger createMenuBurger, GetBurgerById getBurgerById, ListBurgers listBurgers, UpdateMenuBurger updateMenuBurger, UpdateMenuBurgerImage updateMenuBurgerImage, DeleteMenuBurger deleteMenuBurger, SearchMenuBurgers searchMenuBurgers, BurgerRestDtoMapper mapper, ListBurgerIngredients listBurgerIngredients, ProductRestDtoMapper productRestDtoMapper, SearchIngredients searchIngredients, ImageValidator imageValidator) {
        this.createMenuBurger = createMenuBurger;
        this.getBurgerById = getBurgerById;
        this.listBurgers = listBurgers;
        this.updateMenuBurger = updateMenuBurger;
        this.updateMenuBurgerImage = updateMenuBurgerImage;
        this.deleteMenuBurger = deleteMenuBurger;
        this.searchMenuBurgers = searchMenuBurgers;
        this.mapper = mapper;
        this.listBurgerIngredients = listBurgerIngredients;
        this.productRestDtoMapper = productRestDtoMapper;
        this.searchIngredients = searchIngredients;
        this.imageValidator = imageValidator;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/menu", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuBurgerResponseDTO> createMenuBurger(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart("data") CreateMenuBurgerRequestDTO dto,
            @RequestPart(value = "burgerImage", required = false) MultipartFile burgerImage
    ) {
        Integer adminUserId = userDetails.getId();
        boolean hasImage = (burgerImage != null && !burgerImage.isEmpty());

        if (hasImage) {
            imageValidator.validate(burgerImage);
        }

        var command = mapper.toCreateBurgerCommand(dto, burgerImage, adminUserId);
        Burger burger = createMenuBurger.handle(command);

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(burger);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/menu/{idBurger}")
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurger(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger,
            @Valid @RequestBody UpdateMenuBurgerRequestDTO request
    ) {
        Integer adminUserId = userDetails.getId();

        var command = mapper.toUpdateMenuBurgerCommand(idBurger, request, adminUserId);
        Burger updated = updateMenuBurger.handle(command);

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PatchMapping(value = "/menu/{idBurger}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuBurgerResponseDTO> updateMenuBurgerImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger,
            @RequestPart("burgerImage") MultipartFile burgerImage
    ) {
        Integer adminUserId = userDetails.getId();

        imageValidator.validate(burgerImage);

        FileData imageData = FileData.from(burgerImage);
        UpdateMenuBurgerImageCommand command = new UpdateMenuBurgerImageCommand(
                idBurger,
                imageData,
                adminUserId
        );

        Burger updated = updateMenuBurgerImage.handle(command);

        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @DeleteMapping("/menu/{idBurger}")
    public ResponseEntity<MessageResponseDTO> deleteMenuBurger(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idBurger
    ) {
        Integer adminUserId = userDetails.getId();

        deleteMenuBurger.handle(idBurger, adminUserId);

        return ResponseEntity.ok(new MessageResponseDTO(
                "Hamburguesa de menú eliminada exitosamente",
                true
        ));
    }

    @GetMapping("/menu")
    public ResponseEntity<MenuBurgerPageResponseDTO> listMenuBurgers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Burger> burgerPage = listBurgers.handle(pagination);

        MenuBurgerPageResponseDTO response = mapper.toMenuBurgerPageResponseDTO(burgerPage);
        return ResponseEntity.ok(response);
    }

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

        MenuBurgerPageResponseDTO response = mapper.toMenuBurgerPageResponseDTO(result);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/{idBurger}")
    public ResponseEntity<MenuBurgerResponseDTO> getById(
            @PathVariable Integer idBurger
    ) {
        Burger burger = getBurgerById.execute(idBurger);
        MenuBurgerResponseDTO response = mapper.toMenuBurgerResponseDTO(burger);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ingredients")
    public ResponseEntity<BurgerIngredientListDTO> getBurgerIngredients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer categoryId
    ) {
        PaginationRequest pagination = new PaginationRequest(page, size);
        PageResponse<Product> result = listBurgerIngredients.handle(categoryId, pagination);

        return ResponseEntity.ok(productRestDtoMapper.toBurgerIngredientListDTO(result));
    }

    @GetMapping("/ingredients/search")
    public ResponseEntity<BurgerIngredientListDTO> searchIngredients(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaginationRequest pagination = new PaginationRequest(page, size);
        PageResponse<Product> result = searchIngredients.handle(name, pagination);

        return ResponseEntity.ok(productRestDtoMapper.toBurgerIngredientListDTO(result));
    }
}