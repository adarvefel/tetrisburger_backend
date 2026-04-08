// domain/port/out/RecaptchaPort.java
package com.tetris.tetrisburger_backend.domain.port.out;

public interface RecaptchaPort {

    /**
     * Verifica un token de reCAPTCHA v3
     *
     * @param token Token generado en el frontend
     * @param action Acción esperada (login, register, etc.)
     * @return true si es humano, false si es bot
     */
    boolean verifyToken(String token, String action);

    /**
     * Verifica con threshold personalizado
     */
    boolean verifyToken(String token, String action, double customThreshold);
}
