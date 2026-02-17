// infrastructure/adapter/RecaptchaAdapter.java
package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class RecaptchaAdapter implements RecaptchaPort {

    private static final Logger logger = LoggerFactory.getLogger(RecaptchaAdapter.class);

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.verify-url}")
    private String verifyUrl;

    @Value("${recaptcha.threshold}")
    private double threshold;

    private final WebClient webClient;

    public RecaptchaAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public boolean verifyToken(String token, String action) {
        return verifyToken(token, action, threshold);
    }

    @Override
    public boolean verifyToken(String token, String action, double customThreshold) {
        try {
            logger.info("🔐 Verificando reCAPTCHA token para acción: {}", action);

            // Llamar a Google API
            RecaptchaResponse response = webClient.post()
                    .uri(verifyUrl)
                    .bodyValue(buildRequestBody(token))
                    .retrieve()
                    .bodyToMono(RecaptchaResponse.class)
                    .block();

            if (response == null) {
                logger.error("❌ Respuesta nula de Google reCAPTCHA");
                return false;
            }

            logger.info("📊 reCAPTCHA response - Success: {}, Score: {}, Action: {}",
                    response.success, response.score, response.action);

            // Validaciones
            if (!response.success) {
                logger.warn("⚠️ reCAPTCHA falló: {}", response.errorCodes);
                return false;
            }

            if (!action.equals(response.action)) {
                logger.warn("⚠️ Acción no coincide. Esperada: {}, Recibida: {}",
                        action, response.action);
                return false;
            }

            if (response.score < customThreshold) {
                logger.warn("⚠️ Score muy bajo: {} (mínimo: {})",
                        response.score, customThreshold);
                return false;
            }

            logger.info("✅ reCAPTCHA verificado exitosamente. Score: {}", response.score);
            return true;

        } catch (Exception e) {
            logger.error("❌ Error verificando reCAPTCHA: {}", e.getMessage(), e);
            return false;
        }
    }

    private String buildRequestBody(String token) {
        return String.format("secret=%s&response=%s", secretKey, token);
    }

    // DTO para la respuesta de Google
    private static class RecaptchaResponse {

        @JsonProperty("success")
        private boolean success;

        @JsonProperty("score")
        private double score;

        @JsonProperty("action")
        private String action;

        @JsonProperty("challenge_ts")
        private String challengeTs;

        @JsonProperty("hostname")
        private String hostname;

        @JsonProperty("error-codes")
        private String[] errorCodes;

    }
}
