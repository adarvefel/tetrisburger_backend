package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductAlreadyExistsException;
import com.tetris.tetrisburger_backend.domain.exception.ProductCategoryNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.model.ProductCategory;
import com.tetris.tetrisburger_backend.domain.model.Supplier;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProduct;
import com.tetris.tetrisburger_backend.domain.port.in.product.command.UpdateProductCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ProductCategoryRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import com.tetris.tetrisburger_backend.domain.port.out.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateProductUseCase implements UpdateProduct {


    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final SupplierRepository supplierRepository;

    public UpdateProductUseCase(
            ProductRepository productRepository,
            ProductCategoryRepository productCategoryRepository,
            SupplierRepository supplierRepository
    ) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Product update(UpdateProductCommand cmd) {

        Product current = productRepository.findById(cmd.idProduct())
                .orElseThrow(() -> new ProductNotFoundException(cmd.idProduct()));


        ProductCategory category = productCategoryRepository.findById(cmd.productCategoryId())
                .orElseThrow(() -> new ProductCategoryNotFoundException(
                        "Categoría no encontrada con ID: " + cmd.productCategoryId()
                ));

        Supplier supplier = supplierRepository.findById(cmd.supplierId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Proveedor no encontrado con ID: " + cmd.supplierId()
                ));

        // Agrega antes de updateDetails()
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

        Product updated = productRepository.save(current);


        return updated;
    }
}
