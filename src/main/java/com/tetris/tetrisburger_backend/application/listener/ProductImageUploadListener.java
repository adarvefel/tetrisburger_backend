package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.ProductImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductImageUploadListener {

    private static final Logger logger = LoggerFactory.getLogger(ProductImageUploadListener.class);

    private final ProductRepository productRepository;
    private final ImageStoragePort imageStoragePort;

    public ProductImageUploadListener(
            ProductRepository productRepository,
            ImageStoragePort imageStoragePort
    ) {
        this.productRepository = productRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @EventListener
    @Transactional
    public void handleProductImageUpload(ProductImageUploadRequestedEvent event) {
        logger.info("Procesando subida de imagen para producto ID: {}", event.productId());

        try {
            // Crear FileData
            FileData fileData = new FileData(
                    event.originalFilename(),
                    event.contentType(),
                    event.imageBytes()
            );

            // Subir imagen a S3
            ImageUploadResult result = imageStoragePort.uploadProductImage(fileData);

            String imageKey = result.imageKey();  // "products/123-uuid-hamburguesa.jpg"
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            logger.info("Imagen subida exitosamente. Key: {}, URL: {}", imageKey, imageUrl);

            // Buscar el producto
            Product product = productRepository.findById(event.productId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado con ID: " + event.productId()
                    ));

            // Actualizar con imageKey + imageUrl completa
            product.updateImage(imageKey, imageUrl, event.updatedBy());

            productRepository.save(product);
            logger.info("Producto ID {} actualizado con imagen exitosamente", event.productId());

        } catch (Exception e) {
            logger.error("Error al subir imagen para producto ID {}: {}",
                    event.productId(), e.getMessage(), e);
            throw new ImageUploadException("Error al subir imagen del producto", e);
        }
    }

}
