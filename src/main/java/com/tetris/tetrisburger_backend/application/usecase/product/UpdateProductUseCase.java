package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductCategoryNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateProductUseCase implements UpdateProduct {

    private static final Logger logger = LoggerFactory.getLogger(UpdateProductUseCase.class);

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public UpdateProductUseCase(
            ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository
    ) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public Product update(UpdateProductCommand cmd) {
        logger.info(" Actualizando producto ID: {}", cmd.idProduct());

        // Buscar producto actual
        Product current = productRepository.findById(cmd.idProduct())
                .orElseThrow(() -> {
                    logger.error(" Producto no encontrado: ID {}", cmd.idProduct());
                    return new ProductNotFoundException(cmd.idProduct());
                });

        logger.info(" Producto encontrado: '{}' | Categoría actual: '{}'",
                current.getName(),
                current.getCategoryName());

        ProductCategory category = null;
        if (cmd.productCategoryId() != null) {
            category = productCategoryRepository.findById(cmd.productCategoryId())
                    .orElseThrow(() -> {
                        logger.error(" Categoría no encontrada: ID {}", cmd.productCategoryId());
                        return new ProductCategoryNotFoundException(
                                "Categoría no encontrada con ID: " + cmd.productCategoryId()
                        );
                    });
            logger.info(" Nueva categoría: '{}' (ID: {})", category.getName(), category.getId());
        }

        // ✅ Actualizar usando método del dominio
        current.updateDetails(
                cmd.name(),
                cmd.description(),
                cmd.quantity(),
                cmd.price(),
                cmd.availability(),
                cmd.productType(),
                cmd.isBurgerIngredient(),
                category,
                cmd.supplierId(),
                cmd.updatedBy()
        );

        Product updated = productRepository.save(current);
        logger.info("Producto actualizado: ID {} | Nombre: '{}' | Categoría: '{}' | Es ingrediente burger: {}",
                updated.getId(),
                updated.getName(),
                updated.getCategoryName(),
                updated.getIsBurgerIngredient());

        return updated;
    }
}
