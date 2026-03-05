package com.tetris.tetrisburger_backend.application.usecase.burger.admin;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.admin.ToggleMenuBurgerFeatured;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ToggleMenuBurgerFeaturedUseCase implements ToggleMenuBurgerFeatured {

    private static final Logger logger = LoggerFactory.getLogger(ToggleMenuBurgerFeaturedUseCase.class);

    private final BurgerRepository burgerRepository;
    private final UserRepository userRepository;

    public ToggleMenuBurgerFeaturedUseCase(
            BurgerRepository burgerRepository,
            UserRepository userRepository
    ) {
        this.burgerRepository = burgerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Cambia el estado isFavorite de una hamburguesa de menú
     *
     * @param idBurger ID de la hamburguesa
     * @param isFeatured true para marcar como destacada, false para desmarcar
     * @param adminUserId ID del admin/employee que realiza el cambio
     * @return Hamburguesa actualizada
     */
    @Override
    public Burger handle(Integer idBurger, Boolean isFeatured, Integer adminUserId) {
        logger.info(" Toggle favorita menu burger: burgerId={}, isFavorite={}, adminId={}",
                idBurger, isFeatured, adminUserId);

        try {
            // 1. Validaciones
            validateInput(idBurger, isFeatured, adminUserId);
            validateUser(adminUserId);

            // 2. Buscar hamburguesa de menú
            Burger burger = burgerRepository.findActiveMenuById(idBurger)
                    .orElseThrow(() -> new BurgerNotFoundException(
                            "Hamburguesa de menú no encontrada o eliminada. ID: " + idBurger
                    ));

            logger.debug(" Estado actual: name={}, isOnMenu={}, isFavorite={}",
                    burger.getName(), burger.isOnMenu(), burger.isFeatured());

            // 3. Validar que sea burger de menú
            validateIsMenuBurger(burger);

            // 4. Verificar si ya tiene ese estado
            if (burger.isFeatured() == isFeatured) {
                logger.info(" Sin cambios: la hamburguesa ya tiene isFavorite={}", isFeatured);
                return burger;
            }

            // 5. Actualizar estado de favorita
            logger.info("Cambiando estado: from={} to={}", burger.isFeatured(), isFeatured);
            burger.setMenuBurgerFeatured(isFeatured, adminUserId);

            // 6. Guardar
            Burger saved = burgerRepository.save(burger);

            if (saved == null) {
                throw new InvalidBurgerException("Error al guardar estado de favorita");
            }

            logger.info(" Estado de favorita actualizado: burgerId={}, isFavorite={}, updatedBy={}",
                    saved.getIdBurger(), saved.isFeatured(), saved.getUpdatedBy());

            return saved;

        } catch (BurgerNotFoundException | UserNotFoundException | InvalidBurgerException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado en marcar favorita: burgerId={}", idBurger, e);
            throw new InvalidBurgerException("Error cambiando estado de destacada", e);
        }
    }

    // ==================== VALIDACIONES ====================

    private void validateInput(Integer idBurger, Boolean isFeatured, Integer adminUserId) {
        if (idBurger == null) {
            throw new InvalidBurgerException("El ID de la hamburguesa no puede ser nulo");
        }

        if (isFeatured== null) {
            throw new InvalidBurgerException("El estado de favorita no puede ser nulo");
        }

        if (adminUserId == null) {
            throw new InvalidBurgerException("El ID del usuario no puede ser nulo");
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private void validateIsMenuBurger(Burger burger) {
        if (!burger.isMenuBurger()) {
            throw new InvalidBurgerException(
                    "Solo hamburguesas de menú pueden marcarse como destacadas. " +
                            "Burger ID: " + burger.getIdBurger()
            );
        }

        if (burger.isDeleted()) {
            throw new InvalidBurgerException(
                    "No se puede cambiar estado de una hamburguesa eliminada. " +
                            "Burger ID: " + burger.getIdBurger()
            );
        }
    }
}
