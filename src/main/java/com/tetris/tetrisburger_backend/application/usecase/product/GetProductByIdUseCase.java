package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.domain.exception.ProductNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.GetProductById;
import com.tetris.tetrisburger_backend.domain.port.in.product.query.GetProductByIdQuery;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
public class GetProductByIdUseCase implements GetProductById {
    private static final Logger logger = LoggerFactory.getLogger(GetProductByIdUseCase.class);
    private final ProductRepository productRepository;


    public GetProductByIdUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product get(GetProductByIdQuery query) {
        logger.info(" Buscando producto ID: {}", query.id());

        return productRepository.findById(query.id())
                .orElseThrow(() -> {
                    logger.warn(" Producto no encontrado: ID {}", query.id());
                    return new ProductNotFoundException(query.id());
                });
    }

}