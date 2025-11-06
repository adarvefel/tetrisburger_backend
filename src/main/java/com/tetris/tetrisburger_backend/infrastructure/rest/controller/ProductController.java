package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.AdjustProductStock;
import com.tetris.tetrisburger_backend.domain.port.in.product.CreateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.DeleteProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.GetProductById;
import com.tetris.tetrisburger_backend.domain.port.in.product.ListProducts;
import com.tetris.tetrisburger_backend.domain.port.in.product.SearchProducts;
import com.tetris.tetrisburger_backend.domain.port.in.product.SetProductAvailability;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.GetProductByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.ListProductsQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.SearchProductsQuery;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.CreateProductRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ListProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.ProductResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.UpdateProductRequestDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);


    private final CreateProduct createProduct;
    private final UpdateProduct updateProduct;
    private final DeleteProduct deleteProduct;
    private final GetProductById getProductById;
    private final ListProducts listProducts;
    private final SearchProducts searchProducts;
    private final SetProductAvailability setAvailability;
    private final AdjustProductStock adjustStock;


    private final ProductRestDtoMapper mapper;

    public ProductController(CreateProduct createProduct,
                             UpdateProduct updateProduct,
                             DeleteProduct deleteProduct,
                             GetProductById getProductById,
                             ListProducts listProducts,
                             SearchProducts searchProducts,
                             SetProductAvailability setAvailability,
                             AdjustProductStock adjustStock,
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

    // =========================
    // READ (público)
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Integer id) {
        log.debug("GET product id={}", id);
        Product p = getProductById.get(new GetProductByIdQuery(id));
        return ResponseEntity.ok(mapper.toProductResponseDTO(p));
    }

    @GetMapping
    public ResponseEntity<ListProductResponseDTO> list(
            @RequestParam(required = false) Integer productCategoryId,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String direction) {

        log.debug("LIST products page={} size={} sortBy={} direction={}", page, size, sortBy, direction);
        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Product> result = listProducts.list(new ListProductsQuery(productCategoryId, availability), pr);
        return ResponseEntity.ok(mapper.toListProductResponseDTO(result));
    }

    @GetMapping("/search")
    public ResponseEntity<ListProductResponseDTO> search(
            @RequestParam String q,
            @RequestParam(required = false) Integer productCategoryId,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "12") @Min(1) int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String direction) {

        log.debug("SEARCH products q='{}' page={} size={} sortBy={} direction={}", q, page, size, sortBy, direction);
        PaginationRequest pr = new PaginationRequest(page, size, sortBy, direction);
        PageResponse<Product> result = searchProducts.search(new SearchProductsQuery(q, productCategoryId, availability), pr);
        return ResponseEntity.ok(mapper.toListProductResponseDTO(result));
    }

    // =========================
    // (ADMIN)
    // =========================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@AuthenticationPrincipal UserDetails userDetails,
                                                     @Valid @RequestBody CreateProductRequestDTO dto) {
        Integer actorId = extractUserId(userDetails);
        log.info("ADMIN creating product: {} by userId={}", dto.getName(), actorId);
        Product created = createProduct.create(mapper.toCreateProductCommand(dto, actorId));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toProductResponseDTO(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(@AuthenticationPrincipal UserDetails userDetails,
                                                     @PathVariable Integer id,
                                                     @Valid @RequestBody UpdateProductRequestDTO dto) {
        Integer actorId = extractUserId(userDetails);
        log.info("ADMIN updating product id={} by userId={}", id, actorId);
        Product updated = updateProduct.update(mapper.toUpdateProductCommand(id, dto, actorId));
        return ResponseEntity.ok(mapper.toProductResponseDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        log.info("ADMIN deleting product id={}", id);
        deleteProduct.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ProductResponseDTO> setAvailability(@AuthenticationPrincipal UserDetails userDetails,
                                                              @PathVariable Integer id,
                                                              @RequestParam boolean availability) {
        Integer actorId = extractUserId(userDetails);
        log.info("ADMIN set availability id={} -> {} by userId={}", id, availability, actorId);
        Product updated = setAvailability.setAvailability(id, availability, actorId);
        return ResponseEntity.ok(mapper.toProductResponseDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponseDTO> adjustStock(@AuthenticationPrincipal UserDetails userDetails,
                                                          @PathVariable Integer id,
                                                          @RequestParam int delta) {
        Integer actorId = extractUserId(userDetails);
        log.info("ADMIN adjust stock id={} delta={} by userId={}", id, delta, actorId);
        Product updated = adjustStock.adjustStock(id, delta, actorId);
        return ResponseEntity.ok(mapper.toProductResponseDTO(updated));
    }

    // =========================
    // Helpers
    // =========================
    private Integer extractUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails cud) return cud.getId();
        return 0;
    }
}