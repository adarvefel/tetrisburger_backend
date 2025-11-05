// domain/port/out/TokenPort.java
package com.tetris.tetrisburger_backend.domain.port.out;

public interface TokenPort {
    String generateToken(String email);
    long getExpirationTime();
    boolean validateToken(String token);
    String extractEmail(String token);
    String generatePasswordResetToken(String email, long expirationTime);
    boolean validatePasswordResetToken(String token);
    String extractEmailFromPasswordResetToken(String token);
}