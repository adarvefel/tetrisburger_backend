package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Addition;
import com.tetris.tetrisburger_backend.domain.port.in.adittion.*;

import com.tetris.tetrisburger_backend.infrastructure.rest.dto.DeleteResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.AdditionResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.CreateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.addition.UpdateAdditionRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.AdditionRestMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/admin/additions")
@Tag(name = "Additions", description = "Gestión de adiciones del menú")
public class AdditionController {

    private static final Logger logger = LoggerFactory.getLogger(AdditionController.class);

    private final CreateAddition createAddition;
    private final UpdateAddition updateAddition;
    private final UpdateAdditionImage  updateAdditionImage;
    private final ListAddition listAddition;
    private final SearchAdditionByName searchAdditionByName;
    private final GetAdditionById getAdditionById;
    private final DeleteAddition deleteAddition;
    private final AdditionRestMapper mapper;

    public AdditionController(CreateAddition createAddition, UpdateAddition updateAddition, UpdateAdditionImage updateAdditionImage, ListAddition listAddition, SearchAdditionByName searchAdditionByName, GetAdditionById getAdditionById, DeleteAddition deleteAddition, AdditionRestMapper mapper) {
        this.createAddition = createAddition;
        this.updateAddition = updateAddition;
        this.updateAdditionImage = updateAdditionImage;
        this.listAddition = listAddition;
        this.searchAdditionByName = searchAdditionByName;
        this.getAdditionById = getAdditionById;
        this.deleteAddition = deleteAddition;
        this.mapper = mapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdditionResponseDTO> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") CreateAdditionRequestDTO dto,
            @RequestPart(value = "additionImage", required = false) MultipartFile additionImage
    ) {
        logger.info("POST /api/additions - Creando: '{}'", dto.name());

        boolean imageWasSent = (additionImage != null && !additionImage.isEmpty());

        Addition created = createAddition.handle(
                mapper.toCreateAdditionCommand(dto, additionImage)
        );

        AdditionResponseDTO response = mapper.toAdditionResponseDTO(created, imageWasSent);

        logger.info("Adición creada: ID {} | imageStatus: {}",
                created.getIdAddition(),
                response.ImageStatus());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // ==================== ACTUALIZAR ====================

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdditionResponseDTO> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAdditionRequestDTO     dto
    ) {
        logger.info("PUT /api/additions/{}", id);

        Addition updated = updateAddition.handle(
                mapper.toUpdateAdditionCommand(id, dto)
        );

        return ResponseEntity.ok(mapper.toAdditionResponseDTO(updated, false));
    }

    @GetMapping
    public ResponseEntity<PageResponse<AdditionResponseDTO>> getAll(
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, sortDirection);
        PageResponse<Addition> result = listAddition.handle(available, pagination);

        List<AdditionResponseDTO> mappedContent = result.content().stream()
                .map(addition -> mapper.toAdditionResponseDTO(addition, false))
                .toList();

        PageResponse<AdditionResponseDTO> response = new PageResponse<>(
                mappedContent,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdditionResponseDTO> updateImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @RequestPart("additionImage") MultipartFile additionImage
    ) {
        FileData fileData = FileData.from(additionImage);
        Addition updated = updateAdditionImage.handle(id, fileData);

        AdditionResponseDTO response = mapper.toAdditionResponseDTO(updated, true);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<DeleteResponseDTO> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id
    ) {
        Addition deleted = deleteAddition.handle(id);

        return ResponseEntity.ok(new DeleteResponseDTO(
                "Adición eliminada exitosamente",
                true,
                new DeleteResponseDTO.DeletedResourceDTO(
                        deleted.getIdAddition(),
                        deleted.getName()
                )
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdditionResponseDTO> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id
    ) {
        Addition addition = getAdditionById.execute(id);

        return ResponseEntity.ok(
                mapper.toAdditionResponseDTO(addition, false)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<AdditionResponseDTO>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, sortDirection);
        PageResponse<Addition> result = searchAdditionByName.handle(name, pagination);

        List<AdditionResponseDTO> mappedContent = result.content().stream()
                .map(addition -> mapper.toAdditionResponseDTO(addition, false))
                .toList();

        PageResponse<AdditionResponseDTO> response = new PageResponse<>(
                mappedContent,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );

        return ResponseEntity.ok(response);
    }








}
