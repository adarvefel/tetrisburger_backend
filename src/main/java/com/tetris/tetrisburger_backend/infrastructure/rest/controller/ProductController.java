package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.enums.ProductType;

import com.tetris.tetrisburger_backend.domain.port.in.product.*;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.GetProductByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.ListProductsQuery;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.SearchProductsQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.MessageResponseDTO;
import com.tetris.tetrisburger_backend.infrastructure.rest.dto.product.*;
import com.tetris.tetrisburger_backend.domain.port.in.product.ListPublicProducts;
import com.tetris.tetrisburger_backend.infrastructure.rest.mapper.ProductRestDtoMapper;
import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Gestión de productos del restaurante")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final CreateProduct createProduct;
    private final ListProducts listProducts;
    private final GetProductById getProductById;
    private final UpdateProduct updateProduct;
    private final UpdateProductImage updateProductImage;
    private final DeleteProduct deleteProduct;
    private final ProductRestDtoMapper mapper;
    private final ImageStoragePort imageStoragePort;
    private final SearchProducts searchProducts;
    private final SetProductAvailability setProductAvailability;
    private final AdjustProductStock adjustProductStock;
    private final ListPublicProducts listPublicProducts;

    public ProductController(CreateProduct createProduct,
                             ListProducts listProducts,
                             GetProductById getProductById,
                             UpdateProduct updateProduct,
                             UpdateProductImage updateProductImage,
                             DeleteProduct deleteProduct,
                             ProductRestDtoMapper mapper,
                             ImageStoragePort imageStoragePort,
                             SearchProducts searchProducts,
                             SetProductAvailability setProductAvailability,
                             AdjustProductStock adjustProductStock,
                             ListPublicProducts listPublicProducts) {

        this.createProduct = createProduct;
        this.listProducts = listProducts;
        this.getProductById = getProductById;
        this.updateProduct = updateProduct;
        this.updateProductImage = updateProductImage;
        this.deleteProduct = deleteProduct;
        this.mapper = mapper;
        this.imageStoragePort = imageStoragePort;
        this.searchProducts = searchProducts;
        this.setProductAvailability = setProductAvailability;
        this.adjustProductStock = adjustProductStock;
        this.listPublicProducts = listPublicProducts;
    }

    // ==================== HELPERS ====================

    private Integer getUserIdFromDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        throw new IllegalStateException("UserDetails no es CustomUserDetails");
    }

    private String resolveImageUrlFromProduct(Product product) {
        if (product == null || product.getImageKey() == null || product.getImageKey().isBlank()) {
            return null;
        }
        return imageStoragePort.getImageUrl(product.getImageKey());
    }

    private String resolveImageStatus(boolean imageWasSent, String imageUrlResolvedFromProduct) {
        if (imageWasSent) return "PENDING";
        if (imageUrlResolvedFromProduct != null) return "READY";
        return "NONE";
    }

    // ==================== CREATE ====================

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(summary = "Crear producto",
            description = "Crea un nuevo producto con imagen opcional.")
    public ResponseEntity<ProductResponseDTO> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") CreateProductRequestDTO dto,
            @RequestPart(value = "productImage", required = false) MultipartFile productImage
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);
        boolean imageWasSent = (productImage != null && !productImage.isEmpty());

        CreateProductCommand command = mapper.toCreateProductCommand(dto, productImage, adminId);
        Product created = createProduct.create(command);

        String imageUrl = resolveImageUrlFromProduct(created);
        String imageStatus = resolveImageStatus(imageWasSent, imageUrl);

        ProductResponseDTO response = mapper.toProductResponseDTO(created);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageStatus);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== GET ALL ====================

    @GetMapping("/list")
    public ResponseEntity<ListProductResponseDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) Integer productCategoryId,
            @RequestParam(required = false) Boolean availability
    ) {
        ListProductsQuery query = new ListProductsQuery(productCategoryId, availability);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);

        PageResponse<Product> pageResponse = listProducts.list(query, pagination);

        List<ProductResponseDTO> items = pageResponse.content().stream()
                .map(product -> {
                    String imageUrl = resolveImageUrlFromProduct(product);
                    String imageStatus = resolveImageStatus(false, imageUrl);

                    ProductResponseDTO dto = mapper.toProductResponseDTO(product);
                    dto.setImageUrl(imageUrl);
                    dto.setImageStatus(imageStatus);
                    return dto;
                })
                .toList();

        ListProductResponseDTO response = ListProductResponseDTO.builder()
                .items(items)
                .page(pageResponse.page())
                .size(pageResponse.size())
                .totalElements(pageResponse.totalElements())
                .totalPages(pageResponse.totalPages())
                .build();

        return ResponseEntity.ok(response);
    }

    // ==================== SEARCH ====================

    @GetMapping("/search")
    public ResponseEntity<ListProductResponseDTO> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) Integer productCategoryId,
            @RequestParam(required = false) Boolean availability
    ) {
        SearchProductsQuery query = new SearchProductsQuery(q, productCategoryId, availability);
        PaginationRequest pagination = new PaginationRequest(page, size, sortBy, direction);

        PageResponse<Product> pageResponse = searchProducts.search(query, pagination);

        List<ProductResponseDTO> items = pageResponse.content().stream()
                .map(product -> {
                    String imageUrl = resolveImageUrlFromProduct(product);
                    String imageStatus = resolveImageStatus(false, imageUrl);

                    ProductResponseDTO dto = mapper.toProductResponseDTO(product);
                    dto.setImageUrl(imageUrl);
                    dto.setImageStatus(imageStatus);
                    return dto;
                })
                .toList();

        ListProductResponseDTO response = ListProductResponseDTO.builder()
                .build();

        response.setItems(items);
        response.setPage(pageResponse.page());
        response.setSize(pageResponse.size());
        response.setTotalElements(pageResponse.totalElements());
        response.setTotalPages(pageResponse.totalPages());

        return ResponseEntity.ok(response);
    }

    // ==================== GET BY ID ====================

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Integer id) {
        Product product = getProductById.get(new GetProductByIdQuery(id));

        String imageUrl = resolveImageUrlFromProduct(product);
        String imageStatus = resolveImageStatus(false, imageUrl);

        ProductResponseDTO response = mapper.toProductResponseDTO(product);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageStatus);

        return ResponseEntity.ok(response);
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequestDTO dto
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        Product updated = updateProduct.update(
                mapper.toUpdateProductCommand(id, dto, adminId)
        );

        String imageUrl = resolveImageUrlFromProduct(updated);

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageUrl != null ? "READY" : "NONE");

        return ResponseEntity.ok(response);
    }

    // ==================== UPDATE IMAGE ====================

    @PutMapping(value = "/image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> updateImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @RequestPart("productImage") MultipartFile productImage
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        Product updated = updateProductImage.update(id, FileData.from(productImage), adminId);

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(resolveImageUrlFromProduct(updated));
        response.setImageStatus("PENDING");

        return ResponseEntity.ok(response);
    }

    // ==================== AVAILABILITY ====================

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> changeAvailability(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam Boolean availability
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        Product updated = setProductAvailability.setAvailability(id, availability, adminId);

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(resolveImageUrlFromProduct(updated));
        response.setImageStatus(resolveImageStatus(false, response.getImageUrl()));

        return ResponseEntity.ok(response);
    }

    // ==================== STOCK ====================

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> adjustStock(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam Integer delta
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        Product updated = adjustProductStock.adjustStock(id, delta, adminId);

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(resolveImageUrlFromProduct(updated));
        response.setImageStatus(resolveImageStatus(false, response.getImageUrl()));

        return ResponseEntity.ok(response);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MessageResponseDTO> deleteProduct(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        deleteProduct.delete(id, userDetails.getId());
        return ResponseEntity.ok(new MessageResponseDTO("Producto eliminado exitosamente", true));
    }

    // ==================== PUBLIC ====================

    @GetMapping("/public")
    public ResponseEntity<ListProductResponseDTO> getPublicProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ProductType productType,
            @RequestParam(required = false) Integer categoryId
    ) {
        PaginationRequest pagination = new PaginationRequest(page, size, "name", "ASC");

        PageResponse<Product> pageResponse =
                listPublicProducts.list(productType, categoryId, pagination);

        List<ProductResponseDTO> items = pageResponse.content().stream()
                .map(p -> mapper.toPublicProductResponseDTO(p, resolveImageUrlFromProduct(p)))
                .toList();

        ListProductResponseDTO response = ListProductResponseDTO.builder()
                .items(items)
                .page(pageResponse.page())
                .size(pageResponse.size())
                .totalElements(pageResponse.totalElements())
                .totalPages(pageResponse.totalPages())
                .build();

        return ResponseEntity.ok(response);
    }
}