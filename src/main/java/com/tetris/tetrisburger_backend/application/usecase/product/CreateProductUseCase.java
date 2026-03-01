package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.application.event.ProductImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.ProductCategoryNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.SupplierNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.product.CreateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import jakarta.transaction.Transactional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateProductUseCase implements CreateProduct {


    private final ProductRepository productRepository;
    private  final SupplierRepository supplierRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateProductUseCase(ProductRepository productRepository, SupplierRepository supplierRepository, ProductCategoryRepository productCategoryRepository, ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Product create(CreateProductCommand cmd) {
        String productName = cmd.name() != null ? cmd.name().trim() : "";


        // Validar duplicado
        boolean exists = productRepository.existsByNameIgnoreCase(productName);

        if (exists) {
            throw new ProductAlreadyExistsException(productName);
        }


        ProductCategory category = null;
        if (cmd.productCategoryId() != null) {
            category = productCategoryRepository.findById(cmd.productCategoryId())
                    .orElseThrow(() -> new ProductCategoryNotFoundException(
                            "Categoría no encontrada con ID: " + cmd.productCategoryId()
                    ));
        }


        if (cmd.supplierId() == null) {
            throw new SupplierNotFoundException("El proveedor es requerido");
        }

        Supplier supplier = supplierRepository.findById(cmd.supplierId())
                .orElseThrow(() -> new SupplierNotFoundException(
                        "Proveedor no encontrado con ID: " + cmd.supplierId()
                ));

        Product product = Product.create(
                productName,
                cmd.description(),
                cmd.quantity(),
                cmd.price(),
                cmd.availability(),
                cmd.productType(),
                cmd.isBurgerIngredient(),
                category,
                null,                      // imageUrl
                null,                      // imageKey
                supplier,
                cmd.createdBy()
        );

        Product savedProduct = productRepository.save(product);

        // Publicar evento de imagen si viene
        if (cmd.productImageData() != null) {
            eventPublisher.publishEvent(new ProductImageUploadRequestedEvent(
                    savedProduct.getId(),
                    cmd.productImageData().bytes(),
                    cmd.productImageData().contentType(),
                    cmd.productImageData().originalFilename(),
                    cmd.createdBy()
            ));
        }

        return savedProduct;
    }
}
