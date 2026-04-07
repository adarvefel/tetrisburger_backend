package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.application.event.MenuBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.UpdateMenuBurgerImage;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateMenuBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateMenuBurgerImageUseCase implements UpdateMenuBurgerImage {

    private final BurgerRepository burgerRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateMenuBurgerImageUseCase(
            BurgerRepository burgerRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.burgerRepository = burgerRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Burger handle(UpdateMenuBurgerImageCommand command) {
        try {
            validateCommand(command);
            validateUser(command.updatedBy());

            Burger burger = burgerRepository.findActiveMenuById(command.idBurger())
                    .orElseThrow(() -> new BurgerNotFoundException(command.idBurger()));

            if (!burger.isMenuBurger()) {
                throw new InvalidBurgerException(
                        "Solo hamburguesas de menú pueden actualizar imagen. Burger ID: "
                                + command.idBurger()
                );
            }

            eventPublisher.publishEvent(
                    new MenuBurgerImageUploadRequestedEvent(
                            burger.getIdBurger(),
                            command.imageData().bytes(),
                            command.imageData().contentType(),
                            command.imageData().originalFilename(),
                            command.updatedBy()
                    )
            );

            return burger;

        } catch (BurgerNotFoundException | UserNotFoundException |
                 InvalidBurgerException | ImageUploadException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            throw new ImageUploadException("Error al actualizar la imagen de la hamburguesa", e);
        }
    }

    private void validateCommand(UpdateMenuBurgerImageCommand command) {
        if (command == null) {
            throw new InvalidBurgerException("Command no puede ser null");
        }
        if (command.idBurger() == null) {
            throw new InvalidBurgerException("El ID de la hamburguesa no pudo ser encontrado");
        }
        if (command.updatedBy() == null) {
            throw new InvalidBurgerException("El ");
        }
        if (command.imageData() == null) {
            throw new ImageUploadException("Image data cannot be null");
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
