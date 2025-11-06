package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.application.usecase.product.AdjustProductStockUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.CreateProductUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.DeleteProductUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.GetProductByIdUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.ListProductsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.SearchProductsUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.SetProductAvailabilityUseCase;
import com.tetris.tetrisburger_backend.application.usecase.product.UpdateProductUseCase;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.GetProductByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.ListProductsQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.SearchProductsQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.CreateProductRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ListProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.UpdateProductRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final CreateProductUseCase createProduct;
    private final UpdateProductUseCase updateProduct;
    private final DeleteProductUseCase deleteProduct;
    private final GetProductByIdUseCase getProductById;
    private final ListProductsUseCase listProducts;
    private final SearchProductsUseCase searchProducts;
    private final SetProductAvailabilityUseCase setAvailability;
    private final AdjustProductStockUseCase adjustStock;
    private final ProductRestDtoMapper mapper;

    public ProductController(CreateProductUseCase createProduct,
                             UpdateProductUseCase updateProduct,
                             DeleteProductUseCase deleteProduct,
                             GetProductByIdUseCase getProductById,
                             ListProductsUseCase listProducts,
                             SearchProductsUseCase searchProducts,
                             SetProductAvailabilityUseCase setAvailability,
                             AdjustProductStockUseCase adjustStock,
                             ProductRestDtoMapper mapper) {
        this.createProduct = createProduct;
        this.updateProduct = updateProduct;
        this.deleteProduct = deleteProduct;
        this.getProductById = getProductById;
        this.listProducts = listProducts;
        this.searchProducts = searchProducts;
        this.setAvailability = setAvailability;
        this.adjustStock = adjustStock;
        this.mapper = mapper;
    }

    // TODO: obtén el id del usuario autenticado desde el SecurityContext
    private Integer currentUserId() {
        return 0;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Integer id) {
        Product p = getProductById.get(new GetProductByIdQuery(id));
        return ResponseEntity.ok(mapper.toProductResponseDTO(p));
    }

    @GetMapping
    public ResponseEntity<ListProductResponseDTO> list(
            @RequestParam(required = false) Integer productCategoryId,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String direction) {

        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Product> result = listProducts.list(new ListProductsQuery(productCategoryId, availability), pr);
        return ResponseEntity.ok(mapper.toListProductResponseDTO(result));
    }

    @GetMapping("/search")
    public ResponseEntity<ListProductResponseDTO> search(
            @RequestParam String q,
            @RequestParam(required = false) Integer productCategoryId,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String direction) {

        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Product> result = searchProducts.search(new SearchProductsQuery(q, productCategoryId, availability), pr);
        return ResponseEntity.ok(mapper.toListProductResponseDTO(result));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody CreateProductRequestDTO dto) {
        Product created = createProduct.create(mapper.toCreateProductCommand(dto, currentUserId()));
        return ResponseEntity.ok(mapper.toProductResponseDTO(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody UpdateProductRequestDTO dto) {
        Product updated = updateProduct.update(mapper.toUpdateProductCommand(id, dto, currentUserId()));
        return ResponseEntity.ok(mapper.toProductResponseDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        deleteProduct.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ProductResponseDTO> setAvailability(@PathVariable Integer id, @RequestParam boolean availability) {
        Product updated = setAvailability.setAvailability(id, availability, currentUserId());
        return ResponseEntity.ok(mapper.toProductResponseDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDTO> adjustStock(@PathVariable Integer id, @RequestParam int delta) {
        Product updated = adjustStock.adjustStock(id, delta, currentUserId());
        return ResponseEntity.ok(mapper.toProductResponseDTO(updated));
    }
}

