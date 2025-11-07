package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.CreateProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.DeleteProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.GetProductCategoryById;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.ListProductCategories;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.UpdateProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.GetProductCategoryByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.productcategory.query.ListProductCategoriesQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.CreateProductCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ListProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.ProductCategoryResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.productcategory.UpdateProductCategoryRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductCategoryRestDtoMapper;
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
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoryController.class);

    private final CreateProductCategory createCategory;
    private final UpdateProductCategory updateCategory;
    private final DeleteProductCategory deleteCategory;
    private final GetProductCategoryById getById;
    private final ListProductCategories listCategories;
    private final ProductCategoryRestDtoMapper mapper;

    public ProductCategoryController(CreateProductCategory createCategory,
                                     UpdateProductCategory updateCategory,
                                     DeleteProductCategory deleteCategory,
                                     GetProductCategoryById getById,
                                     ListProductCategories listCategories,
                                     ProductCategoryRestDtoMapper mapper) {
        this.createCategory = createCategory;
        this.updateCategory = updateCategory;
        this.deleteCategory = deleteCategory;
        this.getById = getById;
        this.listCategories = listCategories;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> get(@PathVariable Integer id) {
        log.debug("GET product-category id={}", id);
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
        log.debug("LIST product-categories q='{}' page={} size={} sortBy={} dir={}", q, page, size, sortBy, direction);
        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<ProductCategory> result = listCategories.list(new ListProductCategoriesQuery(q), pr);
        return ResponseEntity.ok(mapper.toListResponseDTO(result));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductCategoryResponseDTO> create(@Valid @RequestBody CreateProductCategoryRequestDTO dto) {
        log.info("ADMIN create product-category: {}", dto.getName());
        ProductCategory created = createCategory.create(mapper.toCreateCommand(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDTO> update(@PathVariable Integer id,
                                                             @Valid @RequestBody UpdateProductCategoryRequestDTO dto) {
        log.info("ADMIN update product-category id={}", id);
        ProductCategory updated = updateCategory.update(mapper.toUpdateCommand(id, dto));
        return ResponseEntity.ok(mapper.toResponseDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("ADMIN delete product-category id={}", id);
        deleteCategory.delete(id);
        return ResponseEntity.noContent().build();
    }
}