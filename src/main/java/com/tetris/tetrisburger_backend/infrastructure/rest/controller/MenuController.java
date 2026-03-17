package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.DeleteResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.menu.*;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.MenuRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final CreateMenu createMenu;
    private final UpdateMenu updateMenu;
    private final DeleteMenu deleteMenu;
    private final GetMenuById getMenuById;
    private final ListMenu listMenu;
    private final UpdateMenuImage updateMenuImage;
    private final MenuRestDtoMapper mapper;

    public MenuController(CreateMenu createMenu, UpdateMenu updateMenu,
                          DeleteMenu deleteMenu, GetMenuById getMenuById,
                          ListMenu listMenu, UpdateMenuImage updateMenuImage,
                          MenuRestDtoMapper mapper) {
        this.createMenu = createMenu;
        this.updateMenu = updateMenu;
        this.deleteMenu = deleteMenu;
        this.getMenuById = getMenuById;
        this.listMenu = listMenu;
        this.updateMenuImage = updateMenuImage;
        this.mapper = mapper;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuResponseDTO> create(
            @RequestPart("data") CreateMenuRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        Menu menu = createMenu.handle(
                mapper.toCreateCommand(requestDTO, image, extractUserId(userDetails))
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(menu));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody UpdateMenuRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        Menu menu = updateMenu.handle(
                mapper.toUpdateCommand(id, requestDTO, extractUserId(userDetails))
        );
        return ResponseEntity.ok(mapper.toResponseDTO(menu));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuResponseDTO> updateImage(
            @PathVariable Integer id,
            @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        Menu menu = updateMenuImage.handle(
                mapper.toUpdateImageCommand(id, image, extractUserId(userDetails))
        );
        return ResponseEntity.ok(mapper.toResponseDTO(menu));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponseDTO> delete(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Menu menu = deleteMenu.handle(id, extractUserId(userDetails));
        return ResponseEntity.ok(new DeleteResponseDTO(
                "Menú eliminado correctamente",
                true,
                new DeleteResponseDTO.DeletedResourceDTO(menu.getIdMenu(), menu.getName())
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(mapper.toResponseDTO(getMenuById.handle(id)));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    @GetMapping
    public ResponseEntity<PageResponse<MenuResponseDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        PageResponse<Menu> result = listMenu.handle(
                new PaginationRequest(page, size, sortBy, direction)
        );
        return ResponseEntity.ok(result.map(mapper::toResponseDTO));
    }

    private Integer extractUserId(UserDetails userDetails) {
        return ((CustomUserDetails) userDetails).getId();
    }
}