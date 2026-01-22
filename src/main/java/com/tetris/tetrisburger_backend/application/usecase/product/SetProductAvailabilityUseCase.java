package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.SetProductAvailability;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class SetProductAvailabilityUseCase implements SetProductAvailability {
    private final ProductRepository productRepository;

    public SetProductAvailabilityUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product setAvailability(Integer id, boolean availability, Integer updatedBy) {
        Product current = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Product updated = Product.of(
                current.getId(),
                current.getName(),
                current.getDescription(),
                current.getQuantity(),
                current.getPrice(),
                availability,                            // nuevo valor
                current.getProductType(),
                current.getIngredientType(),
                current.getBurgerIngredient(),           // antes llamabas isBurgerIngredient()
                current.getImageUrl(),                   // incluye imageUrl
                current.getProductCategoryId(),
                current.getSupplierId(),
                current.getCreatedAt(),
                Instant.now(),                           // updatedAt
                current.getDeletedAt(),
                current.getCreatedBy(),
                updatedBy,
                current.getDeletedBy()
        );
        return productRepository.save(updated);
    }

}