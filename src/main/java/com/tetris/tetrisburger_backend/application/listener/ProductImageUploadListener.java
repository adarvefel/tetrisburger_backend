package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.ProductImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.model.Product;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.port.out.ProductRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductImageUploadListener {

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
        try {
            FileData fileData = new FileData(
                    event.originalFilename(),
                    event.contentType(),
                    event.imageBytes()
            );

            ImageUploadResult result = imageStoragePort.uploadProductImage(fileData);

            String imageKey = result.imageKey();
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            Product product = productRepository.findById(event.productId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado con ID: " + event.productId()
                    ));

            product.updateImage(imageKey, imageUrl, event.updatedBy());

            productRepository.save(product);

        } catch (Exception e) {
            throw new ImageUploadException("Error al subir imagen del producto", e);
        }
    }
}
