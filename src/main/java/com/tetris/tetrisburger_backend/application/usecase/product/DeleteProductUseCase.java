package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.DeleteProduct;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@Transactional
public class DeleteProductUseCase implements DeleteProduct {


    private final ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void delete(Integer id, Integer deletedBy) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));


        product.setDeletedAt(LocalDateTime.now());
        product.setDeletedBy(deletedBy);

        productRepository.save(product);

    }
}
