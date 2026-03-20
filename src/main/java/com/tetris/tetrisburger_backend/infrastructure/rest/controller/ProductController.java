package com.tetris.tetrisburger_backend.infrastructure.rest.controller;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.PageResponse;
import com.tetris.tetrisburger_backend.domain.common.PaginationRequest;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductType;

import com.tetris.tetrisburger_backend.domain.port.in.product.*;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Gestión de productos del restaurante")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

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

    public ProductController(CreateProduct createProduct, ListProducts listProducts, GetProductById getProductById, UpdateProduct updateProduct, UpdateProductImage updateProductImage, DeleteProduct deleteProduct, ProductRestDtoMapper mapper, ImageStoragePort imageStoragePort, SearchProducts searchProducts, SetProductAvailability setProductAvailability, AdjustProductStock adjustProductStock, ListPublicProducts listPublicProducts) {
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
    @Operation(
            summary = "Crear producto",
            description = "Crea un nuevo producto con imagen opcional. imageStatus puede ser: NONE, PENDING o READY."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos o imagen inválidos",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Producto duplicado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> create(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Datos del producto (JSON)")
            @Valid @RequestPart("data") CreateProductRequestDTO dto,
            @Parameter(description = "Imagen del producto (opcional, máx 5MB)")
            @RequestPart(value = "productImage", required = false) MultipartFile productImage
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);
        boolean imageWasSent = (productImage != null && !productImage.isEmpty());

        logger.info("🔵 POST /api/products - Admin {} creando: '{}' | Categoría: {}",
                adminId, dto.getName(), dto.getProductCategoryId());

        CreateProductCommand command = mapper.toCreateProductCommand(dto, productImage, adminId);
        Product created = createProduct.create(command);

        String imageUrl = resolveImageUrlFromProduct(created);
        String imageStatus = resolveImageStatus(imageWasSent, imageUrl);

        ProductResponseDTO response = mapper.toProductResponseDTO(created);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageStatus);

        logger.info(" Producto creado: ID {} | Categoría: '{}'",
                created.getId(), created.getCategoryName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== GET ALL ====================

    @GetMapping("/list")
    @Operation(
            summary = "Listar productos",
            description = "Retorna lista paginada de productos con filtros opcionales."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida",
                    content = @Content(schema = @Schema(implementation = ListProductResponseDTO.class)))
    })
    public ResponseEntity<ListProductResponseDTO> getAll(
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Dirección (ASC/DESC)") @RequestParam(defaultValue = "ASC") String direction,
            @Parameter(description = "Filtrar por ID de categoría") @RequestParam(required = false) Integer productCategoryId,
            @Parameter(description = "Filtrar por disponibilidad") @RequestParam(required = false) Boolean availability
    ) {
        logger.info("🔵 GET /api/products/list - Categoría: {}, Disponibilidad: {}",
                productCategoryId, availability);

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

        logger.info("Productos listados: {} resultados", pageResponse.totalElements());
        return ResponseEntity.ok(response);
    }

    // ==================== SEARCH ====================

    @GetMapping("/search")
    @Operation(
            summary = "Buscar productos",
            description = "Busca productos por nombre o descripción con filtros opcionales."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Búsqueda completada",
                    content = @Content(schema = @Schema(implementation = ListProductResponseDTO.class)))
    })
    public ResponseEntity<ListProductResponseDTO> search(
            @Parameter(description = "Texto de búsqueda") @RequestParam(required = false) String q,
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Dirección (ASC/DESC)") @RequestParam(defaultValue = "ASC") String direction,
            @Parameter(description = "Filtrar por ID de categoría") @RequestParam(required = false) Integer productCategoryId,
            @Parameter(description = "Filtrar por disponibilidad") @RequestParam(required = false) Boolean availability
    ) {
        logger.info(" GET /api/products/search - Query: '{}' | Categoría: {}", q, productCategoryId);

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
                .items(items)
                .page(pageResponse.page())
                .size(pageResponse.size())
                .totalElements(pageResponse.totalElements())
                .totalPages(pageResponse.totalPages())
                .build();

        logger.info(" Búsqueda completada: {} resultados", pageResponse.totalElements());
        return ResponseEntity.ok(response);
    }

    // ==================== GET BY ID ====================

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener producto por ID",
            description = "Retorna los detalles de un producto específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> getById(
            @Parameter(description = "ID del producto") @PathVariable Integer id
    ) {
        GetProductByIdQuery query = new GetProductByIdQuery(id);
        Product product = getProductById.get(query);

        String imageUrl = resolveImageUrlFromProduct(product);
        String imageStatus = resolveImageStatus(false, imageUrl);

        ProductResponseDTO response = mapper.toProductResponseDTO(product);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageStatus);

        return ResponseEntity.ok(response);
    }

    // ==================== UPDATE ====================

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza datos del producto (excepto imagen)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto o categoría no encontrado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> update(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID del producto") @PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequestDTO dto
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        logger.info(" PUT /api/products/{} - Admin {} | Categoría: {}",
                id, adminId, dto.getProductCategoryId());

        UpdateProductCommand command = mapper.toUpdateProductCommand(id, dto, adminId);
        Product updated = updateProduct.update(command);

        String imageUrl = resolveImageUrlFromProduct(updated);
        String imageStatus = (imageUrl != null) ? "READY" : "NONE";

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageStatus);

        logger.info(" Producto actualizado: ID {} | Categoría: '{}'",
                updated.getId(), updated.getCategoryName());

        return ResponseEntity.ok(response);
    }

    // ==================== UPDATE IMAGE ====================

    @PutMapping(value = "/image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Actualizar imagen del producto",
            description = "Actualiza únicamente la imagen."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen aceptada (en proceso)",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> updateImage(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "ID del producto") @PathVariable Integer id,
            @Parameter(description = "Nueva imagen (JPG, PNG, WEBP, máx 5MB)")
            @RequestPart("productImage") MultipartFile productImage
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        FileData fileData = FileData.from(productImage);
        Product updated = updateProductImage.update(id, fileData, adminId);

        String imageUrl = resolveImageUrlFromProduct(updated);

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(imageUrl);
        response.setImageStatus("PENDING");

        return ResponseEntity.ok(response);
    }

    // ==================== CHANGE AVAILABILITY ====================

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(summary = "Cambiar disponibilidad", description = "Activa o desactiva un producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> changeAvailability(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam Boolean availability
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        Product updated = setProductAvailability.setAvailability(id, availability, adminId);

        String imageUrl = resolveImageUrlFromProduct(updated);
        String imageStatus = resolveImageStatus(false, imageUrl);

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageStatus);

        return ResponseEntity.ok(response);
    }

    // ==================== ADJUST STOCK ====================

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @Operation(summary = "Ajustar stock", description = "Aumenta o disminuye la cantidad disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stock actualizado",
                    content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(schema = @Schema(implementation = MessageResponseDTO.class)))
    })
    public ResponseEntity<ProductResponseDTO> adjustStock(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer id,
            @Parameter(description = "Cantidad a ajustar (positivo=aumentar, negativo=disminuir)")
            @RequestParam Integer delta
    ) {
        Integer adminId = getUserIdFromDetails(userDetails);

        Product updated = adjustProductStock.adjustStock(id, delta, adminId);

        String imageUrl = resolveImageUrlFromProduct(updated);
        String imageStatus = resolveImageStatus(false, imageUrl);

        ProductResponseDTO response = mapper.toProductResponseDTO(updated);
        response.setImageUrl(imageUrl);
        response.setImageStatus(imageStatus);

        return ResponseEntity.ok(response);
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto mediante soft delete"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<MessageResponseDTO> deleteProduct(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(" DELETE producto ID: {} por usuario ID: {}", id, userDetails.getId());

        deleteProduct.delete(id, userDetails.getId());

        logger.info(" Producto eliminado: ID {}", id);
        return ResponseEntity.ok(new MessageResponseDTO("Producto eliminado exitosamente", true));
    }


    @GetMapping("/public")
    @Operation(summary = "Listar productos públicos",
            description = "Retorna solo BEVERAGE y SIDE para la vista pública")
    public ResponseEntity<ListProductResponseDTO> getPublicProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ProductType productType,
            @RequestParam(required = false) Integer categoryId  // ← agregar
    ) {
        logger.info("GET /api/products/public - productType: {} | categoryId: {}", productType, categoryId);

        PaginationRequest pagination = new PaginationRequest(page, size, "name", "ASC");
        PageResponse<Product> pageResponse = listPublicProducts.list(productType, categoryId, pagination);

        List<ProductResponseDTO> items = pageResponse.content().stream()
                .map(p -> mapper.toPublicProductResponseDTO(p, resolveImageUrlFromProduct(p))) // ← cambio
                .toList();

        ListProductResponseDTO response = ListProductResponseDTO.builder()
                .items(items)
                .page(pageResponse.page())
                .size(pageResponse.size())
                .totalElements(pageResponse.totalElements())
                .totalPages(pageResponse.totalPages())
                .build();

        logger.info("Productos públicos: {} resultados", pageResponse.totalElements());
        return ResponseEntity.ok(response);
    }




}
