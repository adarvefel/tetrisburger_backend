package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.CreateSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.DeleteSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.GetSupplierById;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.ListSuppliers;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.UpdateSupplier;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.GetSupplierByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.supplier.query.ListSuppliersQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.CreateSupplierRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.ListSupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.SupplierResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.supplier.UpdateSupplierRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.SupplierRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final CreateSupplier createSupplier;
    private final UpdateSupplier updateSupplier;
    private final DeleteSupplier deleteSupplier;
    private final GetSupplierById getSupplierById;
    private final ListSuppliers listSuppliers;
    private final SupplierRestDtoMapper mapper;

    public SupplierController(CreateSupplier createSupplier,
                              UpdateSupplier updateSupplier,
                              DeleteSupplier deleteSupplier,
                              GetSupplierById getSupplierById,
                              ListSuppliers listSuppliers,
                              SupplierRestDtoMapper mapper) {
        this.createSupplier = createSupplier;
        this.updateSupplier = updateSupplier;
        this.deleteSupplier = deleteSupplier;
        this.getSupplierById = getSupplierById;
        this.listSuppliers = listSuppliers;
        this.mapper = mapper;
    }

    // GET públicos
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> get(@PathVariable Integer id) {
        Supplier s = getSupplierById.get(new GetSupplierByIdQuery(id));
        return ResponseEntity.ok(mapper.toResponseDTO(s));
    }

    @GetMapping
    public ResponseEntity<ListSupplierResponseDTO> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Supplier> result = listSuppliers.list(new ListSuppliersQuery(q), pr);

        return ResponseEntity.ok(mapper.toListResponseDTO(result));
    }

    // ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateSupplierRequestDTO dto
    ) {
        Integer userId = userDetails.getId();

        Supplier created = createSupplier.create(
                mapper.toCreateCommand(dto, userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSupplierRequestDTO dto) {

        Integer userId = userDetails.getId();

        Supplier updated = updateSupplier.update(
                mapper.toUpdateCommand(id, dto, userId)
        );

        return ResponseEntity.ok(mapper.toResponseDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id
    ) {
        Integer userId = userDetails.getId();

        deleteSupplier.delete(id, userId);

        return ResponseEntity.noContent().build();
    }
}