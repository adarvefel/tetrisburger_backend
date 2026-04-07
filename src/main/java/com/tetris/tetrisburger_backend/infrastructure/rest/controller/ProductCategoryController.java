package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.*;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.GetProductCategoryByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.ListProductCategoriesQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.CreateProductCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ListProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.UpdateProductCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductCategoryRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    private final CreateProductCategory createCategory;
    private final UpdateProductCategory updateCategory;
    private final DeleteProductCategory deleteCategory;
    private final GetProductCategoryById getById;
    private final ListProductCategories listCategories;
    private final ListPublicProductCategories publicProductCategories;
    private final ProductCategoryRestDtoMapper mapper;

    public ProductCategoryController(CreateProductCategory createCategory, UpdateProductCategory updateCategory, DeleteProductCategory deleteCategory, GetProductCategoryById getById, ListProductCategories listCategories, ListPublicProductCategories publicProductCategories, ProductCategoryRestDtoMapper mapper) {
        this.createCategory = createCategory;
        this.updateCategory = updateCategory;
        this.deleteCategory = deleteCategory;
        this.getById = getById;
        this.listCategories = listCategories;
        this.publicProductCategories = publicProductCategories;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> get(@PathVariable Integer id) {
        ProductCategory cat = getById.get(new GetProductCategoryByIdQuery(id));
        return ResponseEntity.ok(mapper.toResponseDTO(cat));
    }

    @GetMapping
    public ResponseEntity<ListProductCategoryResponseDTO> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<ProductCategory> result = listCategories.list(new ListProductCategoriesQuery(q), pr);

        return ResponseEntity.ok(mapper.toListResponseDTO(result));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductCategoryResponseDTO> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateProductCategoryRequestDTO dto
    ) {
        Integer userId = userDetails.getId();

        ProductCategory created = createCategory.create(
                mapper.toCreateCommand(dto, userId)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductCategoryRequestDTO dto
    ) {
        Integer userId = userDetails.getId();

        ProductCategory updated = updateCategory.update(
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

        deleteCategory.delete(id, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/public")
    public ResponseEntity<List<ProductCategoryResponseDTO>> getPublicCategories() {
        List<ProductCategoryResponseDTO> response = publicProductCategories.execute()
                .stream()
                .map(mapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
    }
}