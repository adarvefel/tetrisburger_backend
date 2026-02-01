package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.ResourceNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.ToggleMenuBurgerFavorite;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class ToggleMenuBurgerFavoriteUseCase implements ToggleMenuBurgerFavorite {

    private static final Logger logger = LoggerFactory.getLogger(ToggleMenuBurgerFavoriteUseCase.class);

    private final BurgerRepository burgerRepository;

    public ToggleMenuBurgerFavoriteUseCase(BurgerRepository burgerRepository) {
        this.burgerRepository = burgerRepository;
    }

    /**
     * Cambia el estado isFavorite de una burger de menú
     *
     * @param idBurger ID de la burger
     * @param isFavorite true para marcar como destacada, false para desmarcar
     * @param adminUserId ID del admin/employee que realiza el cambio
     * @return Burger actualizada
     */
    @Override
    public Burger handle(Integer idBurger, Boolean isFavorite, Integer adminUserId) {
        logger.info("Iniciando toggle favorite - idBurger={}, isFavorite={}, adminUserId={}",
                idBurger, isFavorite, adminUserId);

        // Validar parámetros
        if (idBurger == null) {
            logger.error("Error: idBurger es null");
            throw new IllegalArgumentException("El ID de la burger no puede ser null");
        }
        if (isFavorite == null) {
            logger.error("Error: isFavorite es null - idBurger={}", idBurger);
            throw new IllegalArgumentException("isFavorite no puede ser null");
        }
        if (adminUserId == null) {
            logger.error("Error: adminUserId es null - idBurger={}", idBurger);
            throw new IllegalArgumentException("El ID del usuario no puede ser null");
        }

        // Buscar burger
        logger.debug("Buscando burger con ID={}", idBurger);
        Burger burger = burgerRepository.findById(idBurger)
                .orElseThrow(() -> {
                    logger.error("Burger no encontrada - idBurger={}", idBurger);
                    return new ResourceNotFoundException(
                            "Burger no encontrada con ID: " + idBurger
                    );
                });

        logger.debug("Burger encontrada - idBurger={}, name={}, isOnMenu={}, isFavorite={}",
                burger.getIdBurger(), burger.getName(), burger.isOnMenu(), burger.isFavorite());

        // Validar que sea burger de menú
        if (!burger.isOnMenu()) {
            logger.error("Intento de marcar favorita una burger que no es de menú - idBurger={}, isCustom={}",
                    idBurger, burger.isCustom());
            throw new InvalidBurgerException(
                    "Solo burgers de menú pueden marcarse como destacadas. Burger ID: " + idBurger
            );
        }

        // Verificar si ya tiene ese estado
        if (burger.isFavorite() == isFavorite) {
            logger.warn("La burger ya tiene el estado isFavorite={} - idBurger={}, no se realizan cambios",
                    isFavorite, idBurger);
            return burger; // Sin cambios
        }

        // Actualizar estado de favorita
        logger.info("Actualizando estado de favorita - idBurger={}, from={}, to={}, adminUserId={}",
                idBurger, burger.isFavorite(), isFavorite, adminUserId);

        burger.setFavorite(isFavorite, adminUserId);

        // Guardar y retornar
        Burger savedBurger = burgerRepository.save(burger);

        logger.info("Estado de favorita actualizado exitosamente - idBurger={}, isFavorite={}, updatedBy={}",
                savedBurger.getIdBurger(), savedBurger.isFavorite(), savedBurger.getUpdatedBy());

        return savedBurger;
    }
}
