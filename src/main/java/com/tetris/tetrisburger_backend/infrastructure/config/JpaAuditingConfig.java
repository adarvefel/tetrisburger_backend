// infrastructure/config/JpaAuditingConfig.java
package com.tetris.tetrisburger_backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Integer> auditorAware(AuditorAwareImpl auditorAware) {
        return auditorAware;
    }
}