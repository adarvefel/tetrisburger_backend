package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.BurgerIngredient;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.DeleteProduct;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeleteProductUseCase implements DeleteProduct {


    private final ProductRepository productRepository;
    private final BurgerRepository  burgerRepository;

    public DeleteProductUseCase(BurgerRepository burgerRepository, ProductRepository productRepository) {
        this.burgerRepository = burgerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void delete(Integer id, Integer deletedBy) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));


        product.setDeletedAt(LocalDateTime.now());
        product.setDeletedBy(deletedBy);

        productRepository.save(product);
        burgerRepository.findAllByIngredientProductId(id)
                .forEach(burger -> {
                    burger.removeIngredientByProductId(id); // ← reemplaza todo el bloque anterior
                    burgerRepository.save(burger);
                });

    }
}
