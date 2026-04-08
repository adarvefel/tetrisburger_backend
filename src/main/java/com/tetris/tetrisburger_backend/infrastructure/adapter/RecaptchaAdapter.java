package com.tetris.tetrisburger_backend.infrastructure.adapter;

import com.tetris.tetrisburger_backend.domain.port.out.RecaptchaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class RecaptchaAdapter implements RecaptchaPort {

    @Value("${recaptcha.secret-key}")
    private String secretKey;

    @Value("${recaptcha.verify-url}")
    private String verifyUrl;

    @Value("${recaptcha.threshold}")
    private double threshold;

    @Value("${recaptcha.enabled:true}")
    private boolean recaptchaEnabled;

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
        // Si está desactivado, siempre pasa (solo para desarrollo)
        if (!recaptchaEnabled) {
            return true;
        }

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("secret", secretKey);
            formData.add("response", token);

            RecaptchaResponse response = webClient.post()
                    .uri(verifyUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(RecaptchaResponse.class)
                    .block();

            if (response == null) {
                return false;
            }

            if (!response.success) {
                return false;
            }

            if (!action.equals(response.action)) {
                return false;
            }

            if (response.score < customThreshold) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

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
