package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.ProductCategoryNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerIngredientJpaRepository;
import com.tetris.tetrisburger_backend.infrastructure.persistence.repository.BurgerJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UpdateProductUseCase implements UpdateProduct {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final SupplierRepository supplierRepository;
    private final BurgerIngredientJpaRepository burgerIngredientJpa; // ← reemplaza BurgerRepository
    private final BurgerJpaRepository burgerJpa; // ← AGREGA

    public UpdateProductUseCase(
            ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository,
            SupplierRepository supplierRepository,
            BurgerIngredientJpaRepository burgerIngredientJpa,
            BurgerJpaRepository burgerJpa
    ) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.supplierRepository = supplierRepository;
        this.burgerIngredientJpa = burgerIngredientJpa;
        this.burgerJpa = burgerJpa;
    }

    @Override
    public Product update(UpdateProductCommand cmd) {

        Product current = productRepository.findById(cmd.idProduct())
                .orElseThrow(() -> new ProductNotFoundException(cmd.idProduct()));

        ProductCategory category = productCategoryRepository.findById(cmd.productCategoryId())
                .orElseThrow(() -> new ProductCategoryNotFoundException(
                        "Categoría no encontrada con ID: " + cmd.productCategoryId()));

        Supplier supplier = supplierRepository.findById(cmd.supplierId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Proveedor no encontrado con ID: " + cmd.supplierId()));

        if (!current.getName().equalsIgnoreCase(cmd.name().trim())) {
            if (productRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(cmd.name().trim())) {
                throw new ProductAlreadyExistsException(cmd.name());
            }
        }

        current.updateDetails(
                cmd.name(),
                cmd.description(),
                cmd.quantity(),
                cmd.price(),
                cmd.availability(),
                cmd.productType(),
                category,
                supplier,
                cmd.updatedBy()
        );

        Product saved = productRepository.save(current);



        burgerIngredientJpa.updateByProductId(
                cmd.idProduct(),
                cmd.price(),
                cmd.name(),
                current.getImageUrl()
        );

        burgerIngredientJpa.flush(); // ← fuerza el UPDATE antes del recalculo

        burgerJpa.recalculateBasePriceForMenuBurgers(cmd.idProduct());
        burgerJpa.recalculateBasePriceForCustomBurgers(cmd.idProduct());



        return saved;
    }
}