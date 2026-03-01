package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyDeletedException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.SetProductAvailability;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SetProductAvailabilityUseCase implements SetProductAvailability {

    private static final Logger logger = LoggerFactory.getLogger(SetProductAvailabilityUseCase.class);
    private final ProductRepository productRepository;

    public SetProductAvailabilityUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product setAvailability(Integer productId, boolean availability, Integer updatedBy) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductAlreadyDeletedException(productId));

        product.updateAvailability(availability, updatedBy);

        Product updated = productRepository.save(product);

        return updated;
    }

}
