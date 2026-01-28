package com.tetris.tetrisburger_backend.application.usecase.product;

import com.tetris.tetrisburger_backend.application.event.ProductImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.in.product.UpdateProductImage;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class UpdateProductImageUseCase implements UpdateProductImage {

    private static final Logger logger = LoggerFactory.getLogger(UpdateProductImageUseCase.class);

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateProductImageUseCase(ProductRepository productRepository, ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Product update(Integer productId, FileData productImage, Integer updatedBy) {
        logger.info("Iniciando actualización de imagen para producto ID: {}", productId);

        // 1. Validar que la imagen no esté vacía
        if (productImage == null) {
            throw new IllegalArgumentException("La imagen del producto es requerida");
        }

        // 2. Validar tipo de archivo
        String contentType = productImage.contentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        // 3. Validar tamaño (ej: máximo 5MB)
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (productImage.bytes().length > maxSize) {
            throw new IllegalArgumentException("La imagen no puede superar 5MB");
        }

        // 4. Buscar el producto
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado con ID: " + productId));

        // 5. Publicar evento para procesamiento asíncrono
        eventPublisher.publishEvent(new ProductImageUploadRequestedEvent(
                productId,
                productImage.bytes(),
                productImage.contentType(),
                productImage.originalFilename(),
                updatedBy
        ));

        logger.info("Evento ProductImageUploadRequestedEvent publicado para producto ID: {}", productId);
        logger.info("La imagen se procesará de manera asíncrona");

        // 6. Retornar producto actual
        return product;
    }
}
