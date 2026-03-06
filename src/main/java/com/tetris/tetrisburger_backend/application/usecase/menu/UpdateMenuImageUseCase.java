package com.tetris.tetrisburger_backend.application.usecase.menu;

import com.tetris.tetrisburger_backend.domain.common.FileData;
import com.tetris.tetrisburger_backend.domain.common.ImageUploadResult;
import com.tetris.tetrisburger_backend.domain.exception.EntityNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Menu;
import com.tetris.tetrisburger_backend.domain.port.in.menu.UpdateMenuImage;
import com.tetris.tetrisburger_backend.domain.port.in.menu.command.UpdateMenuImageCommand;
import com.tetris.tetrisburger_backend.domain.port.out.ImageStoragePort;
import com.tetris.tetrisburger_backend.domain.port.out.MenuRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateMenuImageUseCase implements UpdateMenuImage {

    private final MenuRepository repository;
    private final ImageStoragePort imageStoragePort;

    public UpdateMenuImageUseCase(MenuRepository repository, ImageStoragePort imageStoragePort) {
        this.repository = repository;
        this.imageStoragePort = imageStoragePort;
    }

    @Override
    public Menu handle(UpdateMenuImageCommand command) {
        Menu menu = repository.findById(command.idMenu())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Menú no encontrado con id: " + command.idMenu()
                ));

        // Eliminar imagen anterior si existe
        if (menu.getImageKey() != null) {
            imageStoragePort.deleteImage(menu.getImageKey());
        }

        FileData fileData = new FileData(
                command.imageData().originalFilename(),
                command.imageData().contentType(),
                command.imageData().bytes()
        );

        ImageUploadResult result = imageStoragePort.uploadMenuImage(fileData);
        String imageUrl = imageStoragePort.getImageUrl(result.imageKey());

        menu.updateImage(imageUrl, result.imageKey());

        return repository.save(menu);
    }
}