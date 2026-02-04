package com.tetris.tetrisburger_backend.application.listener;

import com.tetris.tetrisburger_backend.application.event.CustomBurgerImageUploadRequestedEvent;
import  com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.ImageUploadException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CustomBurgerImageUploadListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomBurgerImageUploadListener.class);
    private static final String CUSTOM_BURGER_FOLDER = "custom-burgers";

    private final ImageStoragePort imageStoragePort;
    private final BurgerRepository burgerRepository;

    public CustomBurgerImageUploadListener(
            ImageStoragePort imageStoragePort,
            BurgerRepository burgerRepository
    ) {
        this.imageStoragePort = imageStoragePort;
        this.burgerRepository = burgerRepository;
    }

    @Async
    @EventListener
    @Transactional
    public void handleCustomBurgerImageUpload(CustomBurgerImageUploadRequestedEvent event) {
        logger.info("Procesando subida de imagen de custom burger para idBurger: {}, idUser: {}",
                event.idBurger(), event.idUser());

        try {
            // 1. Buscar burger
            Burger burger = burgerRepository.findById(event.idBurger())
                    .orElseThrow(() -> new BurgerNotFoundException(event.idBurger()));

            // 2. Verificar que sea custom burger
            if (!burger.isCustom()) {
                logger.error("El burger {} no es personalizado", event.idBurger());
                throw new IllegalStateException(
                        "Solo hamburguesas personalizadas pueden usar este listener. Burger ID: " + event.idBurger()
                );
            }

            // 3. Verificar que el burger pertenece al usuario
            if (!burger.getIdUser().equals(event.idUser())) {
                logger.error("El burger {} no pertenece al usuario {}", event.idBurger(), event.idUser());
                throw new IllegalStateException(
                        "El burger no pertenece al usuario: " + event.idUser()
                );
            }

            logger.debug("Custom burger encontrado: idBurger={}, name={}, idUser={}",
                    burger.getIdBurger(), burger.getName(), burger.getIdUser());

            // 4. Construir FileData desde el evento
            FileData fileData = new FileData(
                    event.originalFileName(),
                    event.contentType(),
                    event.fileBytes()
            );

            // Validar FileData
            if (!fileData.isValid()) {
                logger.error("FileData inválido para custom burger idBurger: {}", event.idBurger());
                throw new ImageUploadException("FileData inválido para burger: " + event.idBurger());
            }

            logger.info("FileData creado para custom burger: originalFilename={}, size={} bytes",
                    fileData.originalFilename(), fileData.bytes().length);

            // 5. Subir la imagen a S3 (carpeta custom-burgers)
            ImageUploadResult uploadResult = imageStoragePort.uploadImage(
                    fileData,
                    CUSTOM_BURGER_FOLDER  // "custom-burgers"
            );

            if (uploadResult == null) {
                logger.error("Error: uploadImage retornó null para custom burger idBurger: {}", event.idBurger());
                throw new ImageUploadException("La subida de imagen falló para burger: " + event.idBurger());
            }

            // ✅ 6. Generar URL completa desde imageKey
            String imageKey = uploadResult.imageKey();
            String imageUrl = imageStoragePort.getImageUrl(imageKey);

            logger.info("Imagen de custom burger subida exitosamente: imageKey={}, imageUrl={}, idBurger={}",
                    imageKey, imageUrl, event.idBurger());

            // ✅ 7. Actualizar con imageUrl completa
            burger.updateImageComplete(
                    imageKey,          // "custom-burgers/123-uuid-miburguer.jpg"
                    imageUrl,          // "https://tetrisburger-images.s3.us-east-1.amazonaws.com/custom-burgers/123-uuid-miburguer.jpg"
                    event.idUser()
            );

            // 8. Guardar cambios
            burgerRepository.save(burger);

            logger.info("Custom burger actualizado con imagen exitosamente: idBurger={}, idUser={}, imageKey={}",
                    event.idBurger(), event.idUser(), imageKey);

        } catch (BurgerNotFoundException e) {
            logger.error("Custom burger no encontrado: idBurger={}", event.idBurger(), e);
            throw e;
        } catch (IllegalStateException e) {
            logger.error("Error de validación: {}", e.getMessage(), e);
            throw new ImageUploadException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error al subir imagen de custom burger para idBurger: {}, idUser: {}",
                    event.idBurger(), event.idUser(), e);
            throw new ImageUploadException(
                    "Error al subir imagen de custom burger: " + e.getMessage(), e
            );
        }
    }



}
