package com.tetris.tetrisburger_backend.application.usecase.burger;

import com.tetris.tetrisburger_backend.domain.exception.BurgerNotFoundException;
import com.tetris.tetrisburger_backend.domain.exception.InvalidBurgerException;
import com.tetris.tetrisburger_backend.domain.exception.UserNotFoundException;
import com.tetris.tetrisburger_backend.domain.model.Burger;
import com.tetris.tetrisburger_backend.domain.port.in.burger.MarkCustomBurgerAsFavorite;
import com.tetris.tetrisburger_backend.domain.port.out.BurgerRepository;
import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MarkCustomBurgerAsFavoriteUseCase implements MarkCustomBurgerAsFavorite {

    private static final Logger logger = LoggerFactory.getLogger(MarkCustomBurgerAsFavoriteUseCase.class);

    private final BurgerRepository burgerRepository;
    private final UserRepository userRepository;

    public MarkCustomBurgerAsFavoriteUseCase(
            BurgerRepository burgerRepository,
            UserRepository userRepository
    ) {
        this.burgerRepository = burgerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(Integer idBurger, Integer idUser) {
        logger.info("🔵 Marcando hamburguesa personalizada como favorita: burgerId={}, userId={}",
                idBurger, idUser);

        try {
            // 1. Validaciones
            validateInput(idBurger, idUser);
            validateUser(idUser);

            // 2. Buscar hamburguesa personalizada del usuario
            Burger burger = burgerRepository.findCustomByIdAndUser(idBurger, idUser)
                    .orElseThrow(() -> new BurgerNotFoundException(
                            "Hamburguesa personalizada no encontrada o no pertenece al usuario. " +
                                    "Burger ID: " + idBurger + ", User ID: " + idUser
                    ));

            // 3. Validar que sea custom burger
            if (!burger.isCustomBurger()) {
                throw new InvalidBurgerException(
                        "Solo hamburguesas personalizadas pueden marcarse como favoritas con este método. " +
                                "Burger ID: " + idBurger
                );
            }

            // 4. Marcar como favorita (el dominio valida todo)
            burger.markAsFavorite(idUser);

            // 5. Guardar
            Burger saved = burgerRepository.save(burger);

            if (saved == null) {
                throw new InvalidBurgerException("Error al guardar hamburguesa como favorita");
            }

            logger.info("✅ Hamburguesa personalizada marcada como favorita: burgerId={}, userId={}",
                    idBurger, idUser);

        } catch (BurgerNotFoundException | UserNotFoundException | InvalidBurgerException e) {
            throw e;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidBurgerException(e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error inesperado marcando hamburguesa como favorita: burgerId={}, userId={}",
                    idBurger, idUser, e);
            throw new InvalidBurgerException("Error marcando hamburguesa como favorita", e);
        }
    }

    // ==================== VALIDACIONES ====================

    private void validateInput(Integer idBurger, Integer idUser) {
        if (idBurger == null) {
            throw new InvalidBurgerException("El ID de la hamburguesa no puede ser nulo");
        }

        if (idUser == null) {
            throw new InvalidBurgerException("El ID del usuario no puede ser nulo");
        }
    }

    private void validateUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
