// src/main/java/com/tetris/tetrisburger_backend/infrastructure/config/AuditorAwareImpl.java
package com.tetris.tetrisburger_backend.infrastructure.config;

import com.tetris.tetrisburger_backend.infrastructure.security.CustomUserDetails;
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

    @Override
    public Optional<Integer> getCurrentAuditor() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null ||
                    !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getPrincipal())) {
                logger.debug("Usuario no autenticado");
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails userDetails) {
                Integer id = userDetails.getId();
                logger.debug("Auditor ID registrado desde principal: {}", id);
                return Optional.of(id);
            }

            logger.warn("Principal no soportado para auditoría: {}", principal.getClass());
            return Optional.empty();

        } catch (Exception e) {
            logger.error("Error obteniendo auditor: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
