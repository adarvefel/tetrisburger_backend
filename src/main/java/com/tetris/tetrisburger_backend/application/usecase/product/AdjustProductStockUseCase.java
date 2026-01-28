package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.AdjustProductStock;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdjustProductStockUseCase implements AdjustProductStock {

    private static final Logger logger = LoggerFactory.getLogger(AdjustProductStockUseCase.class);
    private final ProductRepository productRepository;

    public AdjustProductStockUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product adjustStock(Integer productId, int delta, Integer updatedBy) {
        logger.info(" Admin {} ajustando stock del producto ID {} en: {}",
                updatedBy, productId, delta);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductAlreadyDeletedException(productId));

        int currentQuantity = product.getQuantity();

        // USAR EL MÉTODO DEL DOMAIN MODEL (ya valida stock insuficiente)
        product.adjustStock(delta, updatedBy);

        Product updated = productRepository.save(product);
        logger.info(" Stock del producto ID {} ajustado de {} a {} unidades",
                productId, currentQuantity, updated.getQuantity());

        return updated;
    }

}
