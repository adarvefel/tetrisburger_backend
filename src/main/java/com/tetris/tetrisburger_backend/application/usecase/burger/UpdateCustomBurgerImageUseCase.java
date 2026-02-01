package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.application.event.CustomBurgerImageUploadRequestedEvent;
import com.tetris.tetrisburger_backend.domain.exception.*;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.UpdateCustomBurgerImage;
import com.tetris.tetrisburger_backend.domain.port.in.burger.command.UpdateCustomBurgerImageCommand;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateCustomBurgerImageUseCase implements UpdateCustomBurgerImage {

    private static final Logger logger = LoggerFactory.getLogger(UpdateCustomBurgerImageUseCase.class);

    private final BurgerRepository burgerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateCustomBurgerImageUseCase(
            BurgerRepository burgerRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.burgerRepository = burgerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Burger handle(UpdateCustomBurgerImageCommand command) {
        logger.info("Updating custom burger image: burgerId={}, userId={}",
                command.idBurger(), command.updatedBy());

        // 1. Buscar burger
        Burger burger = burgerRepository.findCustomByIdAndUser(
                        command.idBurger(), command.updatedBy())
                .orElseThrow(() -> new BurgerNotFoundException(command.idBurger()));

        // 2. Publicar evento
        eventPublisher.publishEvent(
                new CustomBurgerImageUploadRequestedEvent(
                        burger.getIdBurger(),
                        command.updatedBy(),
                        command.imageData().bytes(),
                        command.imageData().contentType(),
                        command.imageData().originalFilename()
                )
        );

        logger.info("Image upload event published for burgerId: {}", burger.getIdBurger());

        return burger;
    }
}
