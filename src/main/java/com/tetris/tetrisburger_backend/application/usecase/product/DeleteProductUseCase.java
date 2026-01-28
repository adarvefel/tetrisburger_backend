package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.DeleteProduct;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class DeleteProductUseCase implements DeleteProduct {

    private static final Logger log = LoggerFactory.getLogger(DeleteProductUseCase.class);

    private final ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void delete(Integer id, Integer deletedBy) {
        log.info("Iniciando eliminación del producto con ID: {} por usuario ID: {}", id, deletedBy);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Producto no encontrado con ID: {}", id);
                    return new ProductNotFoundException(id);
                });

        log.debug("Producto encontrado: '{}' (ID: {})", product.getName(), product.getId());

        product.setDeletedAt(Instant.now());
        product.setDeletedBy(deletedBy);

        productRepository.save(product);

        log.info("Producto '{}' (ID: {}) eliminado exitosamente por usuario ID: {}",
                product.getName(), product.getId(), deletedBy);
    }
}
