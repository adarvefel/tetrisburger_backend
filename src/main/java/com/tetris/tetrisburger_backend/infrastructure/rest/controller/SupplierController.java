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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private static final Logger log = LoggerFactory.getLogger(SupplierController.class);

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
        log.debug("GET supplier id={}", id);
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
        log.debug("LIST suppliers q='{}' page={} size={} sortBy={} dir={}", q, page, size, sortBy, direction);
        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Supplier> result = listSuppliers.list(new ListSuppliersQuery(q), pr);
        return ResponseEntity.ok(mapper.toListResponseDTO(result));
    }

    // ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(@Valid @RequestBody CreateSupplierRequestDTO dto) {
        log.info("ADMIN create supplier: {}", dto.getName());
        Supplier created = createSupplier.create(mapper.toCreateCommand(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody UpdateSupplierRequestDTO dto) {
        log.info("ADMIN update supplier id={}", id);
        Supplier updated = updateSupplier.update(mapper.toUpdateCommand(id, dto));
        return ResponseEntity.ok(mapper.toResponseDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("ADMIN delete supplier id={}", id);
        deleteSupplier.delete(id);
        return ResponseEntity.noContent().build();
    }
}