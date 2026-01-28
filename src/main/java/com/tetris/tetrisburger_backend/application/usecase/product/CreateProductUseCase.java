package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.application.event.ProductImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.CreateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.CreateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CreateProductUseCase implements CreateProduct {

    private static final Logger logger = LoggerFactory.getLogger(CreateProductUseCase.class);

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateProductUseCase(
            ProductRepository productRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Product create(CreateProductCommand cmd) {
        String productName = cmd.name() != null ? cmd.name().trim() : "";

        logger.info("🔍 Admin {} creando producto: '{}'", cmd.createdBy(), productName);

        boolean exists = productRepository.existsByNameIgnoreCase(productName);
        logger.info("¿Existe '{}' en BD? → {}", productName, exists);

        if (exists) {
            logger.warn(" RECHAZADO - Producto duplicado: '{}'", productName);
            throw new ProductAlreadyExistsException(productName);
        }

        logger.info(" Validación OK - Procediendo a crear producto '{}'", productName);

        Product product = Product.create(
                productName,
                cmd.description(),
                cmd.quantity(),
                cmd.price(),
                cmd.availability(),
                cmd.productType(),
                cmd.ingredientType(),
                cmd.burgerIngredient(),
                null,
                null,
                cmd.productCategoryId(),
                cmd.supplierId(),
                cmd.createdBy()
        );

        Product savedProduct = productRepository.save(product);
        logger.info("✅ Producto creado con ID: {}", savedProduct.getId());

        if (cmd.productImageData() != null) {
            eventPublisher.publishEvent(new ProductImageUploadRequestedEvent(
                    savedProduct.getId(),
                    cmd.productImageData().bytes(),
                    cmd.productImageData().contentType(),
                    cmd.productImageData().originalFilename(),
                    cmd.createdBy()
            ));
            logger.info(" Evento de imagen publicado para ID: {}", savedProduct.getId());
        }

        return savedProduct;
    }


}
