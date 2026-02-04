package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class MenuBurgerImageUploadListener {

    private static final Logger logger = LoggerFactory.getLogger(MenuBurgerImageUploadListener.class);

    private final BurgerRepository burgerRepository;
    private final ImageStoragePort imageStoragePort;

    public MenuBurgerImageUploadListener(BurgerRepository burgerRepository, ImageStoragePort imageStoragePort) {
        this.burgerRepository = burgerRepository;
        this.imageStoragePort = imageStoragePort;
    }

    @Async
    @EventListener
    @Transactional
    public void handleImageUpload(MenuBurgerImageUploadRequestedEvent event) {
        logger.info("Procesando evento de subida de imagen para idBurger: {}, uploadedBy: {}",
                event.idBurger(), event.uploadedBy());

        try {
            // 1. Buscar burger
            Burger burger = burgerRepository.findById(event.idBurger())
                    .orElseThrow(() -> new BurgerNotFoundException(event.idBurger()));

            logger.debug("Burger encontrado: idBurger={}, name={}",
                    burger.getIdBurger(), burger.getName());

            // 2. Crear FileData desde el evento
            FileData fileData = new FileData(
                    event.originalFileName(),
                    event.contentType(),
                    event.fileBytes()
            );

            // Validar FileData
            if (!fileData.isValid()) {
                logger.error("FileData inválido para idBurger: {}", event.idBurger());
                return;
            }

            logger.info("FileData creado: originalFilename={}, size={} bytes",
                    fileData.originalFilename(), fileData.bytes().length);

            // 3. Subir imagen a S3
            ImageUploadResult result = imageStoragePort.uploadImage(fileData, "burgers");

            if (result == null) {
                logger.error("Error: uploadImage retornó null para idBurger: {}", event.idBurger());
                return;
            }

            // ✅ 4. Generar URL completa desde imageKey
            String imageKey = result.imageKey();
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            logger.info("Imagen subida exitosamente: imageKey={}, imageUrl={}, idBurger={}",
                    imageKey, imageUrl, event.idBurger());

            // ✅ 5. Actualizar burger con imageKey e imageUrl COMPLETA
            burger.updateImageComplete(
                    imageKey,          // "burgers/123-uuid-clasica.jpg"
                    imageUrl,          // "https://tetrisburger-images.s3.us-east-1.amazonaws.com/burgers/123-uuid-clasica.jpg"
                    event.uploadedBy()
            );

            burgerRepository.save(burger);

            logger.info("Burger actualizado con imagen exitosamente: idBurger={}, imageKey={}",
                    event.idBurger(), imageKey);

        } catch (BurgerNotFoundException e) {
            logger.error("Burger no encontrado: idBurger={}", event.idBurger(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error al procesar subida de imagen para idBurger: {}. Error: {}",
                    event.idBurger(), e.getMessage(), e);
            // No lanzar excepción para evitar que falle el proceso asíncrono
        }
    }

}
