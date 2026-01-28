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
        logger.info("==================== getCurrentAuditor LLAMADO ====================");

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                logger.warn("Authentication es NULL");
                return Optional.of(999);  // ✅ Valor temporal para debug
            }

            if (!authentication.isAuthenticated()) {
                logger.warn("Usuario NO autenticado");
                return Optional.of(999);
            }

            if ("anonymousUser".equals(authentication.getPrincipal())) {
                logger.warn("Usuario anónimo");
                return Optional.of(999);
            }

            Object principal = authentication.getPrincipal();
            logger.info("Principal class: {}", principal.getClass().getName());

            if (principal instanceof CustomUserDetails userDetails) {
                Integer id = userDetails.getId();
                logger.info("Auditor ID registrado desde principal: {}", id);
                return Optional.of(id);
            }

            logger.warn("Principal no soportado para auditoría: {}", principal.getClass());
            return Optional.of(999);

        } catch (Exception e) {
            logger.error("Error obteniendo auditor: {}", e.getMessage(), e);
            return Optional.of(999);
        }
    }
}
