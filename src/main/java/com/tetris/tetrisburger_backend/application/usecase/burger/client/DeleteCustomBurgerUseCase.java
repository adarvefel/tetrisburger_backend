//package com.tetris.tetrisburger_backend.application.usecase.burger.user;
//
//import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
//import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
//import com.tetris.tetrisburger_backend.domain.model.Burger;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.user.DeleteCustomBurger;
//import com.tetris.tetrisburger_backend.domain.port.in.burger.command.DeleteCustomBurgerCommand;
//import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//@Transactional
//public class DeleteCustomBurgerUseCase implements DeleteCustomBurger {
//
//    private static final Logger logger = LoggerFactory.getLogger(DeleteCustomBurgerUseCase.class);
//
//    private final BurgerRepository burgerRepository;
//
//    public DeleteCustomBurgerUseCase(BurgerRepository burgerRepository) {
//        this.burgerRepository = burgerRepository;
//    }
//
//    @Override
//    public void handle(DeleteCustomBurgerCommand command) {
//        logger.info("Eliminando custom burger: burgerId={}, userId={}",
//                command.idBurger(), command.idUser());
//
//        try {
//            // 1. Validaciones del command
//            validateCommand(command);
//
//            // 2. Buscar custom burger del usuario
//            Burger burger = burgerRepository.find(
//                            command.idBurger(), command.idUser())
//                    .orElseThrow(() -> new BurgerNotFoundException(
//                            "Custom burger no encontrada o no pertenece al usuario. ID: " + command.idBurger()
//                    ));
//
//            // 3. Validar que sea custom burger (por seguridad)
//            if (!burger.isCustomBurger()) {
//                throw new InvalidBurgerException(
//                        "Solo custom burgers pueden eliminarse con este método. Burger ID: " + command.idBurger()
//                );
//            }
//
//            // 4. Validar que no esté ya eliminada
//            if (burger.getDeletedAt() != null) {
//                throw new InvalidBurgerException(
//                        "La custom burger ya está eliminada. ID: " + command.idBurger()
//                );
//            }
//
//            // 5. Marcar como eliminada (soft delete)
//            burger.markCustomAsDeleted(command.idUser());
//
//            // 6. Guardar
//            burgerRepository.save(burger);
//
//            logger.info("Custom burger eliminada exitosamente: idBurger={}, idBurger={}",
//                    command.idBurger(), command.idUser());
//
//        } catch (BurgerNotFoundException | InvalidBurgerException e) {
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error inesperado eliminando custom burger: idBurger={}",
//                    command.idBurger(), e);
//            throw new InvalidBurgerException("Error eliminando custom burger", e);
//        }
//    }
//
//    private void validateCommand(DeleteCustomBurgerCommand command) {
//        if (command == null) {
//            throw new InvalidBurgerException("Command no deber ser null");
//        }
//
//        if (command.idBurger() == null) {
//            throw new InvalidBurgerException("Burger Id no deber ser null");
//        }
//
//        if (command.idUser() == null) {
//            throw new InvalidBurgerException("User ID no puede ser null");
//        }
//    }
//}
