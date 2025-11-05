package com.tetris.tetrisburger_backend.infrastructure.config;

import com.tetris.tetrisburger_backend.domain.port.out.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(AuditorAwareImpl.class);
    private final UserRepository userRepository;

    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Integer> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Validaciones en una línea
            if (authentication == null ||
                    !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getPrincipal())) {
                logger.debug("Usuario no autenticado");
                return Optional.empty();
            }

            String email = authentication.getName();
            logger.debug("Obteniendo auditor para email: {}", email);

            // Retornar directamente
            return userRepository.findUserByEmail(email)
                    .map(user -> {
                        logger.debug("Auditor ID registrado: {}", user.getIdUser());
                        return user.getIdUser();
                    })
                    .or(() -> {
                        logger.warn("Usuario no encontrado en BD: {}", email);
                        return Optional.empty();
                    });

        } catch (Exception e) {
            logger.error("Error obteniendo auditor: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
